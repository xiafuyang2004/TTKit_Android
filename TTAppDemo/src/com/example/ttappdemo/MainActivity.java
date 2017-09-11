package com.example.ttappdemo;

import com.presenter.BusinessAgent;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import ttkit.network.*;
import ttkit.network.core.*;
import com.presenter.*;
import ttkit.model.*;

//*******************************[实现LoginPresenter类的ILoginListener接口]
public class MainActivity extends Activity implements LoginPresenter.ILoginListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		loginGet();
		loginPersonterTest();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	public void loginGet() {	
		//ͨ通过闭包方式直接调用
		RequestHelper.getInstance().loginByGet(MainActivity.this, "userName","pwd",new NetCallback<UserModel>(){
			@Override
			public void onSuccess(UserModel userModal) {
				Log.e("MainActivity",userModal.toString());
				Toast.makeText(MainActivity.this,String.format("loginGet成功，信息：%s",userModal.target.id),Toast.LENGTH_LONG ).show();
			}
			
			@Override
			public void onFail(String result) {
				Toast.makeText(MainActivity.this,String.format("loginGet失败，信息：%s",result),Toast.LENGTH_LONG ).show();
			}
		});		
	}
	
	public void loginPersonterTest(){
		//addListener在构造函数中初始化，removeListener在关闭view事件中初始化，这里只是示例，全局Presenter时可以复用，但addListener/removeListener需成对使用
		BusinessAgent.getPtr().getLoginPst().addListener(this);
		//BusinessAgent.getPtr().getLoginPst().removeListener(this);
		BusinessAgent.getPtr().getLoginPst().login(MainActivity.this, "userName","pwd");
	}
	
	
	//LoginPresenter的通知回调事件
	@Override
	public void onLoginSuccess(UserModel model){
		Toast.makeText(MainActivity.this,String.format("登录成功，用户id：%s",model.target.id),Toast.LENGTH_LONG ).show();
	}
	
	@Override
	public void onLoginFailed(String errInfo){
		Toast.makeText(MainActivity.this,String.format("登录请求失败，信息：%s",errInfo),Toast.LENGTH_LONG ).show();
	}
	
	@Override
	public void onLoginInfoError(String errInfo){
		Toast.makeText(MainActivity.this,String.format("登录出现错误，信息：%s",errInfo),Toast.LENGTH_LONG ).show();
	}
}
