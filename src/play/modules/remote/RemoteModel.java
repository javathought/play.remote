package play.modules.remote;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.MappedSuperclass;
import javax.swing.ListModel;

//import models.Account;
//import models.RemoteManager;

//import org.codehaus.jackson.JsonParseException;
//import org.codehaus.jackson.map.JsonMappingException;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.type.TypeReference;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

//import ext.remote.RemoteRouter;

import play.Logger;
import play.db.jpa.GenericModel;
import play.db.jpa.Model;
import play.libs.WS;
import play.libs.WS.HttpResponse;

//@MappedSuperclass
public class RemoteModel {

	private static RemoteManager rm = new RemoteManager();

	
    public static interface Factory {

//        public String keyName();
//        public Class<?> keyType();
//        public Object keyValue(Model m);
        public RemoteModel findById(Object id);
//        public List<Model> fetch(int offset, int length, String orderBy, String orderDirection, List<String> properties, String keywords, String where);
//        public Long count(List<String> properties, String keywords, String where);
//        public void deleteAll();
//        public List<Model.Property> listProperties();

    }

    public static RemoteManager rm () {
    	return rm;
    }
	
//	protected static Type listType = new TypeToken<List<?>>() {}.getType();
	
	
	protected static HashMap<String, Type> listMap = new HashMap<String, Type>();
//	protected static RemoteRouter router = new RemoteRouter();
	protected static Gson gson = new Gson();

    private Class<? extends Model> clazz;
    private Map<String, Model.Property> properties;

    /**
     * Find the entity with the corresponding id.
     * @param id The entity id
     * @return The entity
     */
    public static play.modules.remote.RemoteModel findById(Object id) {
        throw new UnsupportedOperationException("Please annotate your Remote model with @play.modules.remote.RemoteEntity annotation.");
    }

    public static <T extends RemoteModel> List<T> findAll() {
        throw new UnsupportedOperationException("Please annotate your Remote model with @play.modules.remote.RemoteEntity annotation.");
    }

    public static <T extends RemoteModel> List<T> find(String query, Object... params) {
        throw new UnsupportedOperationException("Please annotate your Remote model with @play.modules.remote.RemoteEntity annotation.");
    }

    public static Type getCollection() {
        throw new UnsupportedOperationException("Please annotate your Remote model with @play.modules.remote.RemoteEntity annotation.");    	
    }


    
//    public JPAModelLoader(Class<? extends Model> clazz) {
//        this.clazz = clazz;
//    }
//	
//    public Model findById(Object id) {
//        try {
//            if (id == null) {
//                return null;
//            }
//            return JPA.em().find(clazz, id);
//        } catch (Exception e) {
//            // Key is invalid, thus nothing was found
//            return null;
//        }
//    }
	
//	public static <T extends RemoteModel> T findById(Class clazz, long id) {
//    	String s = clazz.getSimpleName() + "s";
//    	String url = "http://localhost:9011/api/accounts/" + id;
//    	HttpResponse response = WS.url(url).get();
//
//    	T pojoaccounts = (T) gson.fromJson(response.getString(), clazz);
//
//    	return pojoaccounts;
//	}
	
//    public RemoteModel findById(Object id) {
//        try {
//            if (id == null) {
//                return null;
//            }
//            return RemoteRouter.rm().find(clazz, id);
//        } catch (Exception e) {
//            // Key is invalid, thus nothing was found
//            return null;
//        }
//    }

	
    @SuppressWarnings("unchecked")
    public List<RemoteModel> fetch(int offset, int size, String orderBy, String order, List<String> searchFields, String keywords, String where) {
        String q = "from " + clazz.getName();
//        if (keywords != null && !keywords.equals("")) {
//            String searchQuery = getSearchQuery(searchFields);
//            if (!searchQuery.equals("")) {
//                q += " where (" + searchQuery + ")";
//            }
//            q += (where != null ? " and " + where : "");
//        } else {
//            q += (where != null ? " where " + where : "");
//        }
//        if (orderBy == null && order == null) {
//            orderBy = "id";
//            order = "ASC";
//        }
//        if (orderBy == null && order != null) {
//            orderBy = "id";
//        }
//        if (order == null || (!order.equals("ASC") && !order.equals("DESC"))) {
//            order = "ASC";
//        }
//        q += " order by " + orderBy + " " + order;
//        Query query = JPA.em().createQuery(q);
//        if (keywords != null && !keywords.equals("") && q.indexOf("?1") != -1) {
//            query.setParameter(1, "%" + keywords.toLowerCase() + "%");
//        }
//        query.setFirstResult(offset);
//        query.setMaxResults(size);
//        return query.getResultList();
        return null;
    }

    public Long count(List<String> searchFields, String keywords, String where) {
//        String q = "select count(*) from " + clazz.getName() + " e";
//        if (keywords != null && !keywords.equals("")) {
//            String searchQuery = getSearchQuery(searchFields);
//            if (!searchQuery.equals("")) {
//                q += " where (" + searchQuery + ")";
//            }
//            q += (where != null ? " and " + where : "");
//        } else {
//            q += (where != null ? " where " + where : "");
//        }
//        Query query = JPA.em().createQuery(q);
//        if (keywords != null && !keywords.equals("") && q.indexOf("?1") != -1) {
//            query.setParameter(1, "%" + keywords.toLowerCase() + "%");
//        }
//        return Long.decode(query.getSingleResult().toString());
    	return null;
    }

//        JPA.em().createQuery("delete from " + clazz.getName()).executeUpdate();
//    public void deleteAll() {
//        
//    }

	
//	public static <T extends RemoteModel> List<T> findAll(Class clazz) {
//    	String s = clazz.getSimpleName() + "s";
//    	Logger.trace("****************************** Type = %s", s);
//    	
////    	Type put = listMap.put(s, new TypeToken<getCollection()>() {}.getType());
//    	Type put = listMap.put(s, getCollection());
//    	String url = "http://localhost:9011/api/accounts";
//    	HttpResponse response = WS.url(url).get();
////		Type listType = new TypeToken<List<Account>>() {}.getType();
//    	Type listType = listMap.get(s);
//
////		Type myType = Remote.class;
//
////		// get the parameterized type, recursively resolving type parameters
////		Type baseType = GenericTypeReflector.getExactSuperType(myType, );
////
////		if (baseType instanceof Class<?>) {
////		        // raw class, type parameters not known
////		        // ...
////		} else {
////		        ParameterizedType pBaseType = (ParameterizedType)baseType;
////		        assert pBaseType.getRawType() == BaseInterface.class; // always true
////		        Type typeParameterForBaseInterface = pBaseType.getActualTypeArguments()[0];
////		        System.out.println(typeParameterForBaseInterface);
////		}
//		
//		
////		Type[] typeParameters = Remote.class.getGenericInterfaces()[0].getActualTypeArguments();
////	    Type[] typeParameters = ((ParameterizedType)List.class).getActualTypeArguments();
////	    Type idType = typeParameters[0]; // Id has only one parameterized type T
//////	    return Id.get((Class)idType, 0L);
//    	List<T> pojoaccounts = gson.fromJson(response.getString(), listType);
//    	
////    	Class<?> t = null;
////    	try {
////    		t = Class.forName("List<model.Account>");
////		} catch (ClassNotFoundException e1) {
////			// TODO Auto-generated catch block
////			e1.printStackTrace();
////		}
////    	List<T> pojoaccounts = (List<T>) gson.fromJson(response.getString(), t);
//    	
//    	//Jackson 
////    	ObjectMapper mapper = new ObjectMapper(); 
////    	List<T> pojoaccounts = mapper.readValue(response.getString(), new TypeReference<List<?>>() { });
////    	List<T> pojoaccounts = mapper.readValue(response.getString(), new TypeReference<List<Account>>() { });
//    	
//    	return (pojoaccounts);
//	}

//	public static <T extends Remote> List<T> findAll() {
//		Type listType = new TypeToken<List<Account>>() {}.getType();
////		Type listType = new TypeToken<List<T>>() {}.getType();
//    	
//    	return findAll(listType);
//	}

	
	  /**
	   * Get the underlying class for a type, or null if the type is a variable type.
	   * @param type the type
	   * @return the underlying class
	   */
	  public static Class<?> getClass(Type type) {
	    if (type instanceof Class) {
	      return (Class) type;
	    }
	    else if (type instanceof ParameterizedType) {
	      return getClass(((ParameterizedType) type).getRawType());
	    }
	    else if (type instanceof GenericArrayType) {
	      Type componentType = ((GenericArrayType) type).getGenericComponentType();
	      Class<?> componentClass = getClass(componentType);
	      if (componentClass != null ) {
	        return Array.newInstance(componentClass, 0).getClass();
	      }
	      else {
	        return null;
	      }
	    }
	    else {
	      return null;
	    }
	  }



//	@Override
//	public void _save() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//	@Override
//	public void _delete() {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//	@Override
//	public Object _key() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	  
	  
}
