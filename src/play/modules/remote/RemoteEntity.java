/**
 * 
 */
package play.modules.remote;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author pascal
 *
 */
// Annotation accessible à l'execution
@Retention(RetentionPolicy.RUNTIME)
// Annotation associé à un type (Classe, interface)
@Target(ElementType.TYPE)
public @interface RemoteEntity {
	String collection();

}
