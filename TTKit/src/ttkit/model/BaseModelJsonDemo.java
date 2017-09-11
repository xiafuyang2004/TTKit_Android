package ttkit.model;

import ttkit.model.base.BaseModelJson;



/**
 * xml模型基类接口(这里只是一个示例).
 * @author xfy
 */
public class BaseModelJsonDemo extends BaseModelJson  {	
	public int 		code;  //0-成功，1-失败，2-token验证失效，请重新登录
	public String   msg;
}
