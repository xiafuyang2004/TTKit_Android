/**
 * 文件名 UserModel.java
 * 包含类名列表 com.issmobile.numlibrary.model
 * 版本信息  版本号 
 * 创建日期 2014年7月4日
 * 版权声明 
 */

package ttkit.model;

/**
*  类名 UserModel
*  @author xfy
*  eg: 登录接口示例
*/

public class UserModel extends BaseModelJsonDemo{
	public class UserModelValue{
		public String id;
	    public String token;
	    public String role;  //登录类型(用户类型:teacher-老师,student-学生)
	    public String school;	    	    
	}	
	public UserModelValue target;
}
