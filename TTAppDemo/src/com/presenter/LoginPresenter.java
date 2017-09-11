package com.presenter;


import android.content.Context;
import ttkit.model.*;
import ttkit.network.*;
import ttkit.presonter.*;
import ttkit.network.core.*;


/**
 * 登录控制层(抽离业务逻辑代码便于复用，或抽离复杂代码进行降偶，这里只是一个示例)
 * 
 * @author 
 */
public class LoginPresenter extends Presonter{
	
	//Ui层事件监听,需要监听当前事件时，需要继承并实现该接口(当实现更多的监听事件时，需要自己实现通知)
	public interface ILoginListener extends IBaseListener {
		void onLoginSuccess(UserModel model);
				
		void onLoginFailed(String errInfo);
		
		void onLoginInfoError(String errInfo);
	}
	
	//网络层回调
	private NetCallback<UserModel> netCallBack = new NetCallback<UserModel>() {
		@Override
		public void onSuccess(UserModel model) {
			if("1".equals(model.code)){
				notityLoginSuccess(model);
			}else{
				notifyLoginFailed(model.msg);
			}
		}

		@Override
		public void onFail(String result) {
			notifyLoginInfoError(result);			
		}		
	};
	
	public void login(Context context,String userName,String pwd) {
		RequestHelper.getInstance().loginByGet(context, userName, pwd,netCallBack);		
	}
	
	
	
	//批量通知事件(循环通知所有的监听对象，观察者模式应用)
	private void notityLoginSuccess(UserModel model) {
        for (IBaseListener listener : mListeners) {
        	((ILoginListener) listener).onLoginSuccess(model);
        }
	}

	private void notifyLoginFailed(String error) {
        for (IBaseListener listener : mListeners) {
        	((ILoginListener) listener).onLoginFailed(error);
        }
	}
	
	private void notifyLoginInfoError(String error) {
        for (IBaseListener listener : mListeners) {
        	((ILoginListener) listener).onLoginInfoError(error);
        }
	}
}
