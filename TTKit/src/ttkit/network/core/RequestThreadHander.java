package ttkit.network.core;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.List;
import ttkit.model.base.BaseModel;


/**
 * 请求线程消息队列，实现数据请求和内容解析，并通知UI(通讯解析框架)
 *
 * @author xfy
 *         <p/>
 *         备注：(UI通知的处理)
 *         问题：在线程中处理完请求和对象序列化之后，需要切换到主线程通知UI,但有可能原有Activity已被关闭(Activity和线程的生命周期不一致),这时要是通知Activity更新UI，会引起异常导致应用崩溃
 *         解决方法：通过建立与Activity的Context对象的弱引用，当Context释放时，不用通知UI;(volley框架原理也是这样实现的)
 *         参考资料：http://blog.csdn.net/matrix_xu/article/details/8424554
 *         http://blog.csdn.net/matrix_xu/article/details/8424038
 *         http://www.cnblogs.com/lwbqqyumidi/p/3769113.html
 */
public class RequestThreadHander extends Handler { 
    private static final String NET_ERROR_PASAL = "数据解析解析失败!";
    private static final String NET_ERROR_CONNECT_FAILED = "请求数据失败 ,请重试!";
    
    public RequestThreadHander(Looper looper) {
        super(looper);
    }

    /**
     * 回调
     */
    public class CallBakRunnable implements Runnable {

        private Object modal = null;
		@SuppressWarnings("rawtypes")
		private NetCallback callback = null;
        private String retErrorInfo = "";
        private boolean isSucessed = false;

		public <T> CallBakRunnable(NetCallback<T> callback, Object object, String retErrorInfo, boolean isSucessed) {
            this.modal = object;
            this.callback = callback;
            this.retErrorInfo = retErrorInfo;
            this.isSucessed = isSucessed;
        }

		@SuppressWarnings("unchecked")
		@Override
        public void run() {
            if (isSucessed  && modal != null) {
                if (callback != null) {
                    callback.onSuccess(modal);
                }
            } else {
                if (callback != null) {
                    callback.onFail(new String(retErrorInfo));
                }
            }
        }

    }


    @Override
    public void handleMessage(Message msg) {
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) msg.obj;
        //msg参数解析
        @SuppressWarnings("unchecked")
        WeakReference<Context> wrContext = (WeakReference<Context>) list.get(0);
        String postData = (String) list.get(1);//请求数据
        BaseModel model = (BaseModel) list.get(2);//对应的实体模对象类型
		NetCallback<?> callback = (NetCallback<?>) list.get(3);//网络回调
        String serverUrl = (String) list.get(4);//服务器地址
        Boolean isPost = ("POST").equals((String) list.get(5)); //请求类型(get/post)
        
        BaseModel retModal = null;
        boolean isSucessed = false;
        String retErrorInfo = "";

        for (int tryCount = 0; tryCount < 3; tryCount++) {
            try {
                //1.请求数据(抛异常则连接失败，这里有3次重试机会)
                String jsonOrXmlStr = "";
                //根据请求类型获取数据
                if (isPost) {
                	jsonOrXmlStr = HttpUrlRequest.doPost(serverUrl,postData);
                } else {
                	jsonOrXmlStr = HttpUrlRequest.doGet(serverUrl);
                }

                //2.数据解析
                retModal = model.pasal(jsonOrXmlStr);
                if(null == retModal){
                	retErrorInfo = NET_ERROR_PASAL;
                	isSucessed = false;
                	Log.e(this.getClass().getSimpleName(), String.format("数据解析失败       modal:%s",model.getClass().getName()));                	
                }else{
                	isSucessed = true;
                }
                //只有网络请求异常时才重试，解析失败时不需要重新请求;
                break;
            } catch (Exception e) {
                retErrorInfo = NET_ERROR_CONNECT_FAILED;
                isSucessed = false;
                Log.e(this.getClass().getSimpleName(), String.format("请求数据失败     重试次数：%s,请求方式： %s,requestData :%s",
                							tryCount,isPost ? "POST":"GET", isPost?postData:serverUrl));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                	Log.e(this.getClass().getSimpleName(),e.getMessage());
                }
            }
        }        
        
        //3.通知更新UI(Activity没有释放时就切换到主线程通知UI)
        Context context = wrContext.get();
        if (null != context) {
            if (context instanceof Activity) {
                try {
                    Activity activity = (Activity) context;
                    if (!activity.isFinishing()) {//是否已被关闭;                        	
                    	activity.runOnUiThread(new CallBakRunnable(callback, retModal, retErrorInfo, isSucessed));                    	
                    }

                    if (activity.isFinishing()) {
                        Log.e(this.getClass().getSimpleName(), "activity is isFinishing");
                    }
                } catch (Exception e) {
                	Log.e(this.getClass().getSimpleName(), e.getMessage());
                }
            }
        }

    }
}


