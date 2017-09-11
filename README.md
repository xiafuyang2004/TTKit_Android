# TTKit_Android 快速开发框架

  **架构原则:** 	
1. 单一原则: 一个函数/类应保证职责单一化
1. 事件驱动UI: 业务层与界面层的通过事件进行交互（都依赖抽象层可以实现解耦，业务层复用性更高，并相对独立，便于单元测试）
1. 高复用性: 支持观察者模式，使独立的业务逻辑能实现高复用性(eg: presonter)
 
  **架构模式:** 
1. MVP

 **框架模块：** 
1. 异步通讯模块(支持并行，串行模式，串行模式通过线程消息队列实现)
1. 接口模型序列化模块(xml/json)
1. MVP模块(提供Presonter定义)
1. 通讯层闭包式回调(NetCallBack)

## 简介

`TTKit_Android`是一款快速开发框架，方便快速搭建App基础开发平台，实现对通讯层，解析层，MVP基础架构的支持；


##工程依赖
1. TTKit       -底层通讯框架
1. TTKitDemo   -Demo演示工程，实现了LoginPresonter(MVP的P层),另外在View层直接调用请求（MVC方式，直接回调到界面层实现对简单业务逻辑的处理); 工程中提供了Json_BookRank.txt和Json_Login.txt接口数据，复制到本地ASPWeb服务器后直接使用(需修改RequestHelper的ServerUrl,ASP服务器建议使用'小旋风AspWeb')

### 1.普通的GET请求,直接在Activity中完成请求回调,通过闭包回调通知(MVC方式)

```java
    RequestHelper.getInstance().loginByGet(MainActivity.this, "userName","pwd",new NetCallback<UserModel>(){
			@Override
			public void onSuccess(UserModel userModal) {
				Toast.makeText(MainActivity.this,String.format("loginGet成功，信息：%s",userModal.target.id),Toast.LENGTH_LONG ).show();
			}
			
			@Override
			public void onFail(String result) {
				Toast.makeText(MainActivity.this,String.format("loginGet失败，信息：%s",result),Toast.LENGTH_LONG ).show();
			}
		});	
```


### 1.普通的GET请求,在Presonter中完成请求回调,通过闭包回调通知(MVP方式,可实现一对多的关联通知)
```
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
        ....
}
```