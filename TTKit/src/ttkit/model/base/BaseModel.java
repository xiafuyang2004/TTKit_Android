package ttkit.model.base;



/**
 * 模型基类接口.
 * @author xfy
 *
 */
public abstract class BaseModel extends Object{
	/**
	 * 序列化XML为实体对象
	 * 
	 * @param obj
	 * @return boolean
	 */
	public abstract BaseModel pasal(String result);
	
	/**
	 * 返回当前实体对象的序列化信息
	 * 
	 * @return String
	 */	
	public abstract String toString();
}