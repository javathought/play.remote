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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import play.Logger;
import play.db.jpa.GenericModel;
import play.db.jpa.Model;
import play.libs.WS;
import play.libs.WS.HttpResponse;

/**
 * A super class for remote JSON entities 
 */
public class RemoteModel {

	private static RemoteManager rm = new RemoteManager();

    public static RemoteManager rm () {
    	return rm;
    }
	
	protected static Gson gson = new Gson();

    /**
     * Find the entity with the corresponding id.
     * @param id The entity id
     * @return The entity
     */
    public static play.modules.remote.RemoteModel findById(Object id) {
        throw new UnsupportedOperationException("Please annotate your Remote model with @play.modules.remote.RemoteEntity annotation.");
    }

    /**
     * Find all the entities of this type
     * @return The entities
     */
    public static <T extends RemoteModel> List<T> findAll() {
        throw new UnsupportedOperationException("Please annotate your Remote model with @play.modules.remote.RemoteEntity annotation.");
    }

    /**
     * Prepare a query to find entities.
     * @param query query shortcut to find in the <code>remote.conf</code> route file
     * @param params Params to bind to the query
     * @return The entities
     */

    public static <T extends RemoteModel> List<T> find(String query, Object... params) {
        throw new UnsupportedOperationException("Please annotate your Remote model with @play.modules.remote.RemoteEntity annotation.");
    }

    /**
     * Delete the entities of this type
     * @return 0/1
     */
    public static HttpResponse delete(Object id) {
        throw new UnsupportedOperationException("Please annotate your Remote model with @play.modules.remote.RemoteEntity annotation.");
    }
  
}
