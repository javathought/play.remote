package play.modules.remote;

import play.Logger;
import play.PlayPlugin;
import play.classloading.ApplicationClasses.ApplicationClass;

public class RemotePlugin extends PlayPlugin {
	
	private RemoteEnhancer enhancer = new RemoteEnhancer(); 
	
	@Override
	   public void onApplicationStart() {
		      Logger.info("*********************** Yeeha, firstmodule started");
		      RemoteRouter.load();
		   }

	@Override
	public void enhance(ApplicationClass applicationClass) throws Exception {

		Logger.trace("*********************** Plugin2 : applicationClass = %s [%s]", applicationClass, applicationClass.toString());
		enhancer.enhanceThisClass(applicationClass);
	}
	

}
