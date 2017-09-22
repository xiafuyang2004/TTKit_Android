package ttkit.network.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;

/**
 * 文件下载
 * @author xfy
 *
 */
public class HttpFileDown {
	/**
     * 下载回调
     */
    public interface DownCallback{
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
	/*
	 * 异步下载主方法
	 */
	public static Thread asynDownFile(final Context context,
									  final String downUrl,
									  final String localPath,
									  final DownCallback callBack) {
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				WeakReference<Context> wrContext = new WeakReference<Context>(context);	
				downloadFile(wrContext,downUrl,localPath,callBack);
			}
			
		});
		thread.start();
		return thread;		
	}
	
	
	private static void downloadFile(final WeakReference<Context> wrContext,
									 final String downUrl,
									 final String localPath,
							 		 final DownCallback callBack) {
		
		Context context = wrContext.get();
				
        String uri = downUrl;
        String savePath= localPath;        
        HttpURLConnection httpConnection = null;
        InputStream is = null;
        FileOutputStream fos = null;
        int totalReadSize = 0;
        int updateTotalSize = 0;
        URL url;

        try {
            File file = new File(savePath);
            if (file.exists()){
                file.delete();
            }

            File decFile = new File(savePath).getParentFile();
            if (decFile != null) {
                decFile.mkdirs();
            }

            url = new URL(uri);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.2+/GameHacker 2; zh-cn)");
            updateTotalSize = httpConnection.getContentLength();
            if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("error download file ~! code:"
                        + httpConnection.getResponseCode());
            }
            is = httpConnection.getInputStream();
            fos = new FileOutputStream(savePath);
            byte[] buffer = new byte[4096];
            int readsize = 0,lastProgress = 0;
            while ((readsize = is.read(buffer)) > 0) {
                fos.write(buffer, 0, readsize);
                totalReadSize += readsize;

                final int iProgress = (int)(((double)totalReadSize /(double) updateTotalSize)*(double)100);
                if(lastProgress  == iProgress) continue;
                
                lastProgress = iProgress;
                Activity activity = (Activity) context;
                if (activity !=null && !activity.isFinishing() && callBack != null) {//是否已被关闭;
                	activity.runOnUiThread(new Runnable(){
        				@Override
        		        public void run() {
        					callBack.onUpdate(downUrl,iProgress);
        				}     
                	});
                }
            }
            fos.flush();
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            is.close();
            fos.close();
            
            Activity activity = (Activity) context;
            if (activity !=null && !activity.isFinishing() && callBack != null) {//是否已被关闭;
            	activity.runOnUiThread(new Runnable(){
    				@Override
    		        public void run() {
    					callBack.onSuccess(downUrl);
    				}     
            	});
            }
        } catch (IOException e) {  
        	e.printStackTrace();
        	
            Activity activity = (Activity) context;
            if (activity !=null && !activity.isFinishing() && callBack != null) {//是否已被关闭;
            	activity.runOnUiThread(new Runnable(){
    				@Override
    		        public void run() {
    					callBack.onFailure(downUrl);
    				}     
            	});
            }
        }
    }
}
