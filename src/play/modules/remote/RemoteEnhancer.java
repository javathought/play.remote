package play.modules.remote;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.AnnotationsAttribute;
import play.Logger;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.classloading.enhancers.Enhancer;

/**
 * Enhance RemoteModel entities classes for JSON API
 */
public class RemoteEnhancer extends Enhancer {

	public static final String PACKAGE_NAME = "play.modules.remote";
	public static final String ENTITY_ANNOTATION_NAME = "play.modules.remote.RemoteEntity";

	@Override
	public void enhanceThisClass(ApplicationClass applicationClass)
			throws Exception {

		final CtClass ctClass = makeClass(applicationClass);

		if (!ctClass.subtypeOf(Enhancer.newClassPool().get(
				"play.modules.remote.RemoteModel"))) {
			return;
		}

		// Enhance MongoEntity annotated classes
		if (!hasAnnotation(ctClass, ENTITY_ANNOTATION_NAME)) {
			return;
		}

		enhanceRemoteEntity(ctClass, applicationClass);
	}

	/**
	 * Enhance classes marked with the MongoEntity annotation.
	 * 
	 * @param ctClass
	 * @throws Exception
	 */
	private void enhanceRemoteEntity(CtClass ctClass,
			ApplicationClass applicationClass) throws Exception {
		// Don't need to fully qualify types when compiling methods below
		classPool.importPackage(PACKAGE_NAME);

		String entityName = ctClass.getName();

		// findById
		CtMethod findById = CtMethod.make(
				"public static RemoteModel findById(Object id) { return ("
						+ entityName + ") rm().find(" + entityName
						+ ".class, id); }", ctClass);
		ctClass.addMethod(findById);
		
		// findAll			
		CtMethod findAll = CtMethod.make("public static java.util.List findAll() { " +
	    	"return rm().findAll(" + entityName + ".class); } ", ctClass);
		ctClass.addMethod(findAll);

        // find
        CtMethod find = CtMethod.make("public static java.util.List find(String query, Object[] params) { return rm().find(" + entityName + ".class, query, params); }", ctClass);
        ctClass.addMethod(find);
		
		// delete			
		CtMethod delete = CtMethod.make("public static play.libs.WS.HttpResponse delete(Object id) { " +
	    	"return rm().delete(" + entityName + ".class, id); } ", ctClass);
		ctClass.addMethod(delete);

		// Done.
		applicationClass.enhancedByteCode = ctClass.toBytecode();
		ctClass.detach();

	}
}
