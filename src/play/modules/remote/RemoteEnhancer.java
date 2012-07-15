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

public class RemoteEnhancer extends Enhancer {

	// public static final String PACKAGE_NAME = "play.modules.mongo";
	public static final String PACKAGE_NAME = "play.modules.remote";

	// public static final String ENTITY_ANNOTATION_NAME =
	// "play.modules.mongo.MongoEntity";
	public static final String ENTITY_ANNOTATION_NAME = "play.modules.remote.RemoteEntity";
	public static final String ENTITY_ANNOTATION_COLLECTION = "collection";

	@Override
	public void enhanceThisClass(ApplicationClass applicationClass)
			throws Exception {

		Logger.trace("*********************** applicationClass = %s",
				applicationClass);

		final CtClass ctClass = makeClass(applicationClass);
		Logger.trace("Enhance class %s/%S : %s ", applicationClass, ctClass,
				ENTITY_ANNOTATION_NAME);

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
		classPool.importPackage("models");

		String entityName = ctClass.getName();

		// Set the default collection name
		String collectionName = "\"" + ctClass.getSimpleName().toLowerCase()
				+ "\"";

		Logger.debug("***************************** using collectionName %s", collectionName);

		AnnotationsAttribute attr = getAnnotations(ctClass);
		Annotation annotation = attr.getAnnotation(ENTITY_ANNOTATION_NAME);
		 if (annotation.getMemberValue(ENTITY_ANNOTATION_COLLECTION) != null){
		 collectionName =
		 annotation.getMemberValue(ENTITY_ANNOTATION_COLLECTION).toString();
		 }
		 
//		 javassist.expr.Expr expr = new javassist.expr.Expr collectionName

		Logger.debug("***************************** " + this.getClass().getName() + "-->enhancing RemoteEntity-->"
				+ ctClass.getName() + "-->collection-->" + collectionName);

		// findById
		ctClass.addMethod(CtMethod.make(
				"public static RemoteModel findById(Object id) { return ("
						+ entityName + ") rm().find(" + entityName
						+ ".class, id); }", ctClass));

		// findAll				
		String s = 					    "public static java.util.List findAll() { " +
		    	"return rm().findAll(" + entityName + ".class); } ";
//				    	"return rm().findAll(" + entityName + ".class, " + entityName + "s.class); } ";

		Logger.trace("***************************** Enhance Compile = %s", s);
		ctClass.addMethod(CtMethod.make(s, ctClass));

        // find
        CtMethod find = CtMethod.make("public static java.util.List find(String query, Object[] params) { return rm().find(" + entityName + ".class, query, params); }", ctClass);
        ctClass.addMethod(find);
		
//				ctClass.addMethod(CtMethod.make(
//					    "public static java.util.List<" + entityName + "> findAll() { " +
//					    	"return rm().findAll(" + entityName + ".class, new com.google.gson.reflect.TypeToken<java.util.List<" + entityName + ">>() {}.getType()); } ", ctClass));
//				"public static java.util.List<" + entityName + "> findAll() { return (java.util.List<"
//						+ entityName + ">) rm().findAll(" + entityName
//						+ ".class); }", ctClass));

		// getCollection
//		s = "public static java.lang.reflect.Type getCollection() { return new com.google.gson.reflect.TypeToken<java.util.List<" + entityName + ">>(){}.getType(); }";
//		Logger.trace("***************************** Enhance Compile = %s", s);
//		ctClass.addMethod(CtMethod.make(
//				"public static Type getCollection() { return new TypeToken<List<" + entityName + ">>(){}.getType(); }", ctClass));

		// Done.
		applicationClass.enhancedByteCode = ctClass.toBytecode();
		ctClass.detach();

	}
}
