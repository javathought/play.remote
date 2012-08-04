package play.modules.remote;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import play.Logger;
import play.Play;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.modules.remote.RemoteRouter.Route;
import play.mvc.Controller;
import play.mvc.Http.Header;
import play.mvc.results.NotFound;

public class RemoteManager {
	protected static Gson gson = new Gson();
	public static HashMap<String, Type>listMap = new HashMap<String, Type>(); 
	private String host;
	private String port;	
	private String urlBase;
	
	public RemoteManager() {
		reload();
	}
	
	public void reload() {
		host = Play.configuration.getProperty("remote.host");
		port = Play.configuration.getProperty("remote.port","9000");
		urlBase = "http://" + host + ":" + port;		
	}


	/**
	 * 
	 * @param clazz
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public RemoteModel find(Class clazz, Object id) {
		Route route = getIdUrl(clazz);
		Object[] params = new Object[1]; params[0] = id;
		String url = urlBase + bindParameters(route.path, params);

		if (route.method.equals("GET")) {
	    	HttpResponse response = WS.url(url).get();			
	    	return (RemoteModel) gson.fromJson(response.getString(), clazz);
		}
		return null;

	}
	
	@SuppressWarnings("unchecked")
	public <T extends RemoteModel>List<T> findAll(Class clazz) {
		Route route = getAllUrl(clazz);
		String url = String.format(urlBase + route.path);

		if (route.method.equals("GET")) {
			return get(clazz, url);
		}
		return null;
		
	}
	

	public <T extends RemoteModel>List<T> find(Class clazz, String query, Object[] params) {
		Route route = getUrl(clazz, query);
		String url = urlBase + bindParameters(route.path, params);

		if (route.method.equals("GET")) {
			return get(clazz, url);
		}
		return null;		
	}


	public HttpResponse delete(Class clazz, Object id) {
		Route route = RemoteRouter.route(clazz.getSimpleName()+".delete");
		Object[] params = new Object[1]; params[0] = id;
		String url = urlBase + bindParameters(route.path, params);

		if (Logger.isTraceEnabled()) {
			Logger.trace("%s path = [%s]", route.method, route.path);
		}
		
		if (route.method.equals("DELETE")) {
	    	HttpResponse response = WS.url(url).delete();
	    	return response;
		}
		throw new NotFound(route.path);		
	}

	private <T extends RemoteModel>List<T> get(Class clazz, String url) {
	    	HttpResponse response = WS.url(url).get();	    	
	    	ArrayList<T> list = new ArrayList<T> ();
	    	
	    	JsonElement element = response.getJson();
	    	JsonArray array = element.getAsJsonArray();
	    	for (int i = 0; i < array.size(); i++) {
				list.add( (T) gson.fromJson(array.get(i), clazz) );
			}
	    	return list;
	}

	public HttpResponse persist(RemoteModel remoteModel) {
		Route route = getUrl(remoteModel.getClass(), "save");
		String url = urlBase + route.path;

		if (route.method.equals("PUT") || route.method.equals("POST") ) {
	    	HttpResponse response = WS.url(url).body(gson.toJson(remoteModel)).post();
	    	return response;
		}
		throw new NotFound(route.path);		
	}

	public HttpResponse post(RemoteModel remoteModel, String query, Object[] params) {
		Route route = getUrl(remoteModel.getClass(), query);
		String url = urlBase + bindParameters(route.path, params);

		if (route.method.equals("*") || route.method.equals("POST") ) {
	    	HttpResponse response = WS.url(url).body(gson.toJson(remoteModel)).post();
	    	return response;
		}
		throw new NotFound(route.path);		
	}	
	
	private String bindParameters(String path, Object[] params) {
		int i = 0; 				// index in the params array
		int start = 0, pos = 0; // indexes for the place holders in the path
		
		StringBuffer s = new StringBuffer();
		while ((start = path.indexOf('{', pos)) > -1) {
			s.append(path.substring(pos, start));
			s.append(params[i++]);
			pos = path.indexOf('}', start)+1;
		}
		s.append(path.substring(pos, path.length()));
		return s.toString();
	}

	public Route getUrl(Class clazz, String query) {
		return RemoteRouter.route(clazz.getSimpleName()+"." +query);
	}

	public Route getIdUrl(Class clazz) {
		return RemoteRouter.route(clazz.getSimpleName()+".findById");
	}

	public Route getAllUrl(Class clazz) {
		return RemoteRouter.route(clazz.getSimpleName()+".findAll");
	}

}
