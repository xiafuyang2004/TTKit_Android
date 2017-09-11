package com.presenter;


/**
 * 公用业务层(用于管理全局或通用的业务层对象)
 * 
 * @author 
 * @date 2015-07-06
 */
public class BusinessAgent {
	private static BusinessAgent ptr = new BusinessAgent();	
	public static BusinessAgent getPtr(){
		return ptr;
	}
	
	private LoginPresenter loginPresenter = new LoginPresenter();
	private BusinessAgent(){		
	}
	
	public LoginPresenter getLoginPst(){
		return loginPresenter;
	}
	
	
	

}
