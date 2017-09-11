package ttkit.network.core;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ttkit.model.base.BaseModel;


/**
 * 线程控制器
 *
 * @author xfy
 */
public class RequestThreadControl {
    private static RequestThreadControl threadConnorPtr = new RequestThreadControl();
    private HandlerThread handlerThread = null;              //即时消息处理线程
    private HandlerThread handlerSecondThread = null;        //非即时处理消息线程(根据需要可以增加线程，以增加请求的即时响应)
    private RequestThreadHander firstHandler = null;
    private RequestThreadHander secondHandler = null;

    private RequestThreadControl() {
        handlerThread = new HandlerThread("handlerThread");
        handlerThread.start();
        firstHandler = new RequestThreadHander(handlerThread.getLooper());
        handlerSecondThread = new HandlerThread("handlerSecondThread");
        handlerSecondThread.start();
        secondHandler = new RequestThreadHander(handlerSecondThread.getLooper());
    }

    public static RequestThreadControl getInstance() {
        return threadConnorPtr;
    }    

    /**
     * 发送到请求线程获取消息
     *
     * @param context     activity弱引用
     * @param requestData 请求数据
     * @param retModal    解析后返回的实体模型对象
     * @param callback    网络层回调
     */
    public <T> void sendToThreadByPost(Context context,String serverUrl,String postData,BaseModel model, NetCallback<T> callback) {
        List<Object> list = new ArrayList<Object>();
        WeakReference<Context> wrContext = new WeakReference<Context>(context);
        list.add(wrContext);
        list.add(postData);
        list.add(model);
        list.add(callback);
        list.add(serverUrl);
        list.add("POST");

        Message msg = firstHandler.obtainMessage();
        msg.obj = list;
        msg.sendToTarget();
    }


    /**
     * 发送到请求线程获取消息(辅助线程)
     *
     * @param context     activity弱引用
     * @param requestData 请求数据
     * @param retModal    解析后返回的实体模型对象
     * @param callback    网络层回调
     */
    public <T> void sendToSecondThreadByPost(Context context,String serverUrl,String postData,BaseModel model, NetCallback<T> callback) {
        List<Object> list = new ArrayList<Object>();
        WeakReference<Context> wrContext = new WeakReference<Context>(context);
        list.add(wrContext);
        list.add(postData);
        list.add(model);
        list.add(callback);
        list.add(serverUrl);
        list.add("POST");

        Message msg = secondHandler.obtainMessage();
        msg.obj = list;
        msg.sendToTarget();
    }
    
    /**
     * 发送到请求线程获取消息
     *
     * @param context     activity弱引用
     * @param requestData 请求数据
     * @param retModal    解析后返回的实体模型对象
     * @param callback    网络层回调
     */
    public <T> void sendToThreadByGet(Context context,String serverUrl,BaseModel modal, NetCallback<T> callback) {
        List<Object> list = new ArrayList<Object>();
        WeakReference<Context> wrContext = new WeakReference<Context>(context);
        list.add(wrContext);
        list.add("postData"); //保持参数格式一致
        list.add(modal);
        list.add(callback);
        list.add(serverUrl);
        list.add("GET");

        Message msg = firstHandler.obtainMessage();
        msg.obj = list;
        msg.sendToTarget();        
    }


    /**
     * 发送到请求线程获取消息(辅助线程)
     *
     * @param context     activity弱引用
     * @param requestData 请求数据
     * @param retModal    解析后返回的实体模型对象
     * @param callback    网络层回调
     */
    public <T> void sendToSecondThreadByGet(Context context,String serverUrl,BaseModel model, NetCallback<T> callback) {
        List<Object> list = new ArrayList<Object>();
        WeakReference<Context> wrContext = new WeakReference<Context>(context);
        list.add(wrContext);
        list.add("postData"); //保持参数格式一致
        list.add(model);
        list.add(callback);
        list.add(serverUrl);
        list.add("GET");

        Message msg = secondHandler.obtainMessage();
        msg.obj = list;
        msg.sendToTarget();
    }
    
    
    /**
     * 发送到请求线程获取消息(并行方式,创建新线程去发送)
     *
     * @param context     activity弱引用
     * @param requestData 请求数据
     * @param retModal    解析后返回的实体模型对象
     * @param callback    网络层回调
     */
    public <T> void sendToNewThread(Context context,String serverUrl,boolean isPost,String postData,BaseModel model, NetCallback<T> callback) {
    	HandlerThread handlerThread  = new HandlerThread(serverUrl);
    	handlerThread.start();
        final RequestThreadHander handler = new RequestThreadHander(handlerThread.getLooper());
            	
    	List<Object> list = new ArrayList<Object>();
        WeakReference<Context> wrContext = new WeakReference<Context>(context);
        list.add(wrContext);
        list.add(postData);
        list.add(model);
        list.add(callback);
        list.add(serverUrl);
        list.add(isPost ? "POST":"GET");

        Message msg = handler.obtainMessage();
        msg.obj = list;
        msg.sendToTarget(); 
                
        handler.post(new Runnable(){
        	@Override
            public void run() {
        		handler.getLooper().quit();
        	}
        });
    }

    /**
     * 开始请求线程
     */
    public void start() {
        //在使用HandlerThread的getLooper()方法之前，必须先调用该类的start();  
        handlerThread.start();
        handlerSecondThread.start();
    }

    /**
     * 退出请求线程
     */
    public void exitThread() {
        handlerThread.getLooper().quit();
        handlerSecondThread.getLooper().quit();
    }
}









