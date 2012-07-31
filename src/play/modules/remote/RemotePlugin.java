package play.modules.remote;

import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.modules.remote.RemoteEnhancer;

public class RemotePlugin extends PlayPlugin {
	
	private RemoteEnhancer enhancer = new RemoteEnhancer();
    static Long lastLoading = 0L;

	
	@Override
	public void onApplicationStart() {
		Logger.info("Remote module started");
		RemoteRouter.load();
		RemoteModel.rm().reload();
        lastLoading = System.currentTimeMillis();
	}

	@Override
	public void enhance(ApplicationClass applicationClass) throws Exception {
		enhancer.enhanceThisClass(applicationClass);
	}
	
	@Override
	public void detectChange() {
        if (Play.getVirtualFile("conf/messages")!=null && Play.getVirtualFile("conf/messages").lastModified() > lastLoading) {
            onApplicationStart();
            return;
        }
        if (Play.getVirtualFile("conf/remote.conf")!=null && Play.getVirtualFile("conf/remote.conf").lastModified() > lastLoading) {
            onApplicationStart();
            return;
        }
	}

}
