package play.modules.remote;

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
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.modules.remote.RemoteRouter.Route;

public class RemoteManager {
	protected static Gson gson = new Gson();
	public static HashMap<String, Type>listMap = new HashMap<String, Type>(); 


	/**
	 * 
	 * @param clazz
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public RemoteModel find(Class clazz, Object id) {
		Route route = getIdUrl(clazz);
		String path = route.path;
		Logger.trace("Manager : route = %s", path);
		String paramRoute = path.replaceFirst("\\{.*\\}", "%d");
		Logger.trace("Manager : paramRoute = %s", paramRoute);
		String url = String.format("http:/" + paramRoute , id);
		Logger.trace("Manager : url = %s", url);
//    	String url = "http://localhost:9011/api/accounts/" + id;
		if (route.method.equals("GET")) {
	    	HttpResponse response = WS.url(url).get();			
	    	return (RemoteModel) gson.fromJson(response.getString(), clazz);
		}
		return null;

	}
	
	@SuppressWarnings("unchecked")
	public <T extends RemoteModel>List<T> findAll(Class clazz) {
		Route route = getAllUrl(clazz);
//		String path = route.path;
//		Logger.trace("Manager : route = %s", path);
//		String paramRoute = path.replaceFirst("\\{.*\\}", "%d");
//		Logger.trace("Manager : paramRoute = %s", paramRoute);
		
		String url = String.format("http:/" + route.path);
		Logger.trace("Manager : url = %s", url);
//    	String url = "http://localhost:9011/api/accounts/" + id;
//		Type type = listMap.get(clazz.getName());
//		Type type = new TypeToken<List<Account>>() {}.getType();
//		clazz.
		if (route.method.equals("GET")) {
			return get(clazz, url);
		}
		return null;
		
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

	public <T extends RemoteModel>List<T> find(Class clazz, String query, Object[] params) {
		Route route = getUrl(clazz, query);
		String url = "http:/" + bindParameters(route.path, params);
		if (route.method.equals("GET")) {
			return get(clazz, url);
		}
		return null;
		
	}


	private String bindParameters(String path, Object[] params) {
		// TODO Auto-generated method stub
//		String paramRoute = path.replaceFirst("\\{.*\\}", "%d");
		int i = 0; // index in the params array
		int start = 0, pos = 0; // indexes for the place holders in the path
		
		StringBuffer s = new StringBuffer();
		while ((start = path.indexOf('{', pos)) > -1) {
			s.append(path.substring(pos, start));
			s.append(params[i++]);
			pos = path.indexOf('}', start)+1;
		}
		s.append(path.substring(pos, path.length()));
		Logger.trace("******************** url binded = %s ", s);
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
