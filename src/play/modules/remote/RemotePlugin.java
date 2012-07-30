package play.modules.remote;

import play.Logger;
import play.PlayPlugin;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.modules.remote.RemoteEnhancer;

public class RemotePlugin extends PlayPlugin {
	
	private RemoteEnhancer enhancer = new RemoteEnhancer(); 
	
	@Override
	   public void onApplicationStart() {
		      Logger.info("Remote module started");
		      RemoteRouter.load();
		   }

	@Override
	public void enhance(ApplicationClass applicationClass) throws Exception {
		enhancer.enhanceThisClass(applicationClass);
	}
	

}
