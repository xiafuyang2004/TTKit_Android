package ttkit.network.core;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * HttpURLConnection请求类
 *
 * @author xfy
 */
public class HttpUrlRequest {

    /**
     * 连接超时时间
     */
    private static final int CONNECT_TIMEOUT = 6 * 1000;
    /**
     * 读取超时时间
     */
    private static final int READ_TIMEOUT = 6 * 1000;


    /**
     * Post Request
     *
     * @param requestData
     * @param requestUrl
     * @return
     * @throws Exception
     */
    public static String doPost(String requestUrl,String requestData) throws Exception {
    	OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;
        HttpURLConnection httpURLConnection = null;
        
        try {
        	URL requestURL = new URL(requestUrl);        	
        	httpURLConnection = (HttpURLConnection)requestURL.openConnection();
	        	        
	        httpURLConnection.setRequestMethod("POST");
	        httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
	        httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);  // 设置连接超时时间
			httpURLConnection.setReadTimeout(READ_TIMEOUT);			
			httpURLConnection.setUseCaches(false);  			// Post请求不能使用缓存   	
			httpURLConnection.setDoOutput(true);

            outputStream = httpURLConnection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);
            
            outputStreamWriter.write(requestData);
            outputStreamWriter.flush();
            
            if (httpURLConnection.getResponseCode() >= 300) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }
            
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
            reader = new BufferedReader(inputStreamReader);
            
            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
            
        } finally{
	        if (outputStreamWriter != null) {
	            outputStreamWriter.close();
	        }	        
	        if (outputStream != null) {
	            outputStream.close();
	        }	        
	        if (reader != null) {
	            reader.close();
	        }	        
	        if (inputStreamReader != null) {
	            inputStreamReader.close();
	        }	        
	        if (inputStream != null) {
	            inputStream.close();
	        }
	        if (httpURLConnection != null){
	        	httpURLConnection.disconnect();
	        }
        }            
        return resultBuffer.toString();
    }


    /**
     * get Request
     *
     * @param requestUrl
     * @return
     * @throws Exception
     */
    public static String doGet(String requestUrl) throws Exception {
    	InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;
        HttpURLConnection httpURLConnection = null;
        
        try {
        	URL requestURL = new URL(requestUrl);        	
        	httpURLConnection = (HttpURLConnection)requestURL.openConnection();
	        	        
	        httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);  // 设置连接超时时间
			httpURLConnection.setReadTimeout(READ_TIMEOUT);
			httpURLConnection.connect();
                             
            if (httpURLConnection.getResponseCode() >= 300) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }
            
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
            
            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }            
        } finally{             
	        if (reader != null) {
	            reader.close();
	        }	        
	        if (inputStreamReader != null) {
	            inputStreamReader.close();
	        }	        
	        if (inputStream != null) {
	            inputStream.close();
	        }
	        if (httpURLConnection != null){
	        	httpURLConnection.disconnect();
	        }
        }            
        return resultBuffer.toString();
    }

}
