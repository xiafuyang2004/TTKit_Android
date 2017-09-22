package ttkit.presonter;

import ttkit.network.core.HttpFileDown;
import ttkit.network.core.ThreadTaskMannager;
import android.content.Context;
import android.text.TextUtils;

/**
 * 下载逻辑类
 */
public class FileDownPresonter extends Presonter {
	/**
     * 下载回调
     */
    public interface DownloadProcessCallback extends IBaseListener {
        /**
         * 下载错误
         */
        void onFailure(String downUrl);

        /**
         * 下载成功
         */
        void onSuccess(String downUrl);

        /**
         * 进度更新  0%~100%
         * 
         * @return
         */
        void onUpdate(String downUrl, int iProgress);

    }        
    
    private ThreadTaskMannager taskMannager = new ThreadTaskMannager();
    private Context mContext = null;
    
    private static FileDownPresonter ptr = null;
    public static FileDownPresonter getInstance(){
    	if(null == ptr){
    		ptr = new FileDownPresonter();
    	}
    	return ptr;
    }
    
    
    /**      
     * 初始化UI切换对象,需要是全局UI对象    
     * @param context
     */
    public void initContext(Context context){
    	this.mContext = context;
    }
    
    
    /**
     * 异步下载
     *
     * @param  bookEntity
     * @return boolean
     */
    public boolean downloadFileAsync(String downUrl,String localPath) {
        if (TextUtils.isEmpty(downUrl) || isTaskAlive(downUrl)){
            return false;
        }
        
        //TODO 添加业务代码
        taskMannager.addTask(downUrl,
			        		HttpFileDown.asynDownFile(mContext, downUrl,localPath,new HttpFileDown.DownCallback(){
			
								@Override
								public void onFailure(String downUrl) {
									//TODO 添加业务代码
									taskMannager.removeTask(downUrl);
									notifyFailed(downUrl);
								}
			
								@Override
								public void onSuccess(String downUrl) {
									//TODO 添加业务代码
									taskMannager.removeTask(downUrl);
									notifySuccess(downUrl);
								}
			
								@Override
								public void onUpdate(String downUrl, int iProgress) {
									notifyUpdate(downUrl,iProgress);
								}
			        		}));
        return true;
    }
    
    
    public void removeDownloadTask(String downUrl) {
    	taskMannager.removeTask(downUrl);    	
    	//取消task的同时通知下载失败
    	notifyFailed(downUrl);
    }
    
    public boolean isTaskAlive(String downUrl){
    	return taskMannager.isTaskAlive(downUrl);
    }
    
    
    
    /**
     * 下载成功通知
     *
     */
    protected void notifySuccess(String downUrl) {
        for (IBaseListener listener : mListeners) {
        	try{
        		((DownloadProcessCallback) listener).onSuccess(downUrl);
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        }
    }

    /**
     * 下载失败通知
     *
     */
    protected void notifyFailed(String downUrl) {
        for (IBaseListener listener : mListeners) {
        	try{
	        	((DownloadProcessCallback) listener).onFailure(downUrl);
	        }catch(Exception ex){
	    		ex.printStackTrace();
	    	}
        }
    }
    /**
     * 更新进度通知
     *
     */
    protected void notifyUpdate(String downUrl, int iProgress) {
        for (IBaseListener listener : mListeners) {
        	try{
        		((DownloadProcessCallback) listener).onUpdate(downUrl,iProgress);
	        }catch(Exception ex){
	    		ex.printStackTrace();
	    	}
        }
    }
}
