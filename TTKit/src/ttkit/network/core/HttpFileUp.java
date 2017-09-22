package ttkit.network.core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;

/**
 * 文件上传
 * @author xfy
 *
 */
public class HttpFileUp {
	/**
	 * 文件上传回调
	 * @author xfy
	 *
	 */
	public interface FileUpCallback{
	    /**
	     * 返回成功信息.
	     * @param result    返回具体内容
	     */
	    void onSuccess(String upUrl,String result);
	    /**
	     * 返回错误信息.
	     * @param result    返回连接失败信息
	     */
	    void onFail(String upUrl,String error);
	    /**
	     * 返回成功信息.
	     * @param result    返回上传进度
	     */
	    void onUpProgress(String upUrl,int iProgress);
	}

	
	public static  void asynUpFileToServer(final Context context,
										   final String postUrl,
										   final String path,
										   final FileUpCallback callBack){
		new Thread(new Runnable(){
			@Override
	        public void run() {
				WeakReference<Context> wrContext = new WeakReference<Context>(context);				
				try{					
					final String result  = upFileByPost(wrContext,postUrl,path,callBack);
					
					Activity activity = (Activity) wrContext.get();
	                if (activity !=null && !activity.isFinishing() && callBack != null) {//是否已被关闭;
	                	activity.runOnUiThread(new Runnable(){
	        				@Override
	        		        public void run() {
	        					callBack.onSuccess(postUrl,result);
	        				}     
	                	});
	                }
				}catch(Exception e){
					e.printStackTrace();
					
					final String error = e.toString();
					Context context = wrContext.get();
					Activity activity = (Activity) context;
	                if (activity !=null && !activity.isFinishing() && callBack != null) {//是否已被关闭;
	                	activity.runOnUiThread(new Runnable(){
	        				@Override
	        		        public void run() {
	        					callBack.onFail(postUrl,error);
	        				}     
	                	});
	                }
				}								
			}
		}).start();
	}
	
	private static String upFileByPost(WeakReference<Context> wrContext,
									   final String postUrl,
									   String path,
									   final FileUpCallback callBack) throws IOException {  
		Context context = wrContext.get();
		
		String end = "\r\n";  
        String twoHyphens = "--";  
        String boundary = "******";  
        URL url = new URL(postUrl);  
        HttpURLConnection httpURLConnection = (HttpURLConnection) url  
                .openConnection();  
        httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K  
        // 允许输入输出流  
        httpURLConnection.setDoInput(true);  
        httpURLConnection.setDoOutput(true);  
        httpURLConnection.setUseCaches(false);  
        // 使用POST方法  
        httpURLConnection.setRequestMethod("POST");  
        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");  
        httpURLConnection.setRequestProperty("Charset", "UTF-8");  
        httpURLConnection.setRequestProperty("Content-Type",  
                "multipart/form-data;boundary=" + boundary);  
  
        DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());  
        //先传参数头:token值
        dos.writeBytes(twoHyphens + boundary + end);  
        dos.writeBytes(String.format("Content-Disposition: form-data; name=\"token\" %s",end));    
        dos.writeBytes(String.format("Content-Type: text/plain %s",end));
        dos.writeBytes(end);
        dos.writeBytes(String.format("%s %s","token",end));//TODO  有参数的加在这里
        dos.writeBytes(twoHyphens + boundary + end);                  
        dos.writeBytes(String.format("Content-Disposition: form-data; name=\"file\"; filename=\"%s\" %s",path.substring(path.lastIndexOf("/") + 1),end)); 
        dos.writeBytes(String.format("Content-Type: image/jpeg %s",end));
        dos.writeBytes(end);
  
        FileInputStream fis = new FileInputStream(path);
        File filePtr = new File(path);		
        long fileSize = filePtr.length();
        long upCount = 0;
        byte[] buffer = new byte[8192]; // 8k  
        int count = 0;                        
        // 读取文件  
        while ((count = fis.read(buffer)) != -1) {  
            dos.write(buffer, 0, count);
                        
            upCount += count;
            final int iProgress = (int)(((double)upCount/(double)fileSize)*(double)100);
			Activity activity = (Activity) context;
            if (activity !=null && !activity.isFinishing() && callBack != null) {//是否已被关闭;
            	activity.runOnUiThread(new Runnable(){
    				@Override
    		        public void run() {
    					callBack.onUpProgress(postUrl,iProgress);
    				}     
            	});
            }
        }  
        fis.close();  
        dos.writeBytes(end);  
        dos.writeBytes(twoHyphens + boundary + twoHyphens + end);  
        dos.flush();  
        InputStream is = httpURLConnection.getInputStream();  
        InputStreamReader isr = new InputStreamReader(is, "utf-8");  
        BufferedReader br = new BufferedReader(isr);  
        String result = br.readLine();  
        dos.close();  
        is.close();  
        return result;
    }  
}
