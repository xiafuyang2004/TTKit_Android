package ttkit.network;


import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import ttkit.model.*;
import ttkit.network.core.NetCallback;
import ttkit.network.core.RequestThreadControl;

/**
 * API请求助手类
 *
 * @author
 */
public class RequestHelper {
	public static final String SERVERURL = "http://10.100.144.51";	
	
    private static RequestHelper Ptr = new RequestHelper();
    public static RequestHelper getInstance() {
        return Ptr;
    }	
    /*
     * 获取请求线程对象
     */
    private RequestThreadControl getSendThreadPtr() {
        return RequestThreadControl.getInstance();
    }
    /*
     * 组装post参数列表
     */
    @SuppressWarnings("deprecation")
	public String getPostData(Map<String, String> paramMap){
    	String postData = "";
    	paramMap.put("token",getToken());
    	for (Map.Entry<String, String> entry : paramMap.entrySet()) {
    		postData += String.format("&%s=%s", entry.getKey(),URLEncoder.encode(entry.getValue()));
    	}
    	return postData;
    }
    
    /*
     * 组装Get方式服务器地址
     */
    @SuppressWarnings("deprecation")
	public String getUrl(Map<String, String> paramMap){
    	String url = String.format("%s?", SERVERURL);
    	paramMap.put("token",getToken());
    	for (Map.Entry<String, String> entry : paramMap.entrySet()) {
    		url += String.format("&%s=%s", entry.getKey(),URLEncoder.encode(entry.getValue()));
    	}
    	return url;
    }
    /*
     * 获取当前token值
     */
    public String getToken(){
    	return "";
    }
        
    /*
     * 组装Get方式服务器地址(测试方法)
     */
    @SuppressWarnings("deprecation")
	public String getTestUrl(String server,Map<String, String> paramMap){
    	String url = String.format("%s?", server);
    	paramMap.put("token",getToken());
    	for (Map.Entry<String, String> entry : paramMap.entrySet()) {
    		url += String.format("&%s=%s", entry.getKey(),URLEncoder.encode(entry.getValue()));
    	}
    	return url;
    }
    /**
     * 登录(eg:Post)
     *
     * @param context
     * @param callback
     */
    public void loginByPost(Context context,String userName,String pwd,NetCallback<UserModel> callback) {    
    	HashMap<String, String> paramMap = new HashMap<String, String>();
    	paramMap.put("account",userName);
    	paramMap.put("password",pwd);    	
    	
    	String test_SERVERURL = "http://10.100.144.51/Json_login.txt";
    	getSendThreadPtr().sendToThreadByPost(context,test_SERVERURL,getPostData(paramMap), new UserModel(), callback);
    }
    /**
     * 登录(eg:Get)
     *
     * @param context
     * @param callback
     */
    public void loginByGet(Context context,String userName,String pwd,NetCallback<UserModel> callback) {
    	HashMap<String, String> paramMap = new HashMap<String, String>();
    	paramMap.put("account",userName);
    	paramMap.put("password",pwd);
    	
    	String test_SERVERURL = "http://10.100.144.51/Json_login.txt";
    	getSendThreadPtr().sendToNewThread(context,getTestUrl(test_SERVERURL,paramMap),false,"", new UserModel(), callback);
    }

    
    /**
     * 排行榜(eg:Get)
     *
     * @param context
     * @param callback
     */
    public void getBookRank(Context context,int page,int size,NetCallback<BookRankModel> callback) {
    	HashMap<String, String> paramMap = new HashMap<String, String>();
    	paramMap.put("page",String.valueOf(page));
    	paramMap.put("size",String.valueOf(size));    	
    	
    	String test_SERVERURL = "http://10.100.144.51/Json_BookRank.txt";
    	getSendThreadPtr().sendToThreadByGet(context,getTestUrl(test_SERVERURL,paramMap), new BookRankModel(), callback);
    }







}
