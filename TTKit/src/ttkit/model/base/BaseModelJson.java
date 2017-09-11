package ttkit.model.base;

import ttkit.Serialize.JSONSerializeHelper;

/**
 * xml模型基类接口.
 * @author xfy
 *
 */
public class BaseModelJson extends BaseModel{
	/**
	 * 序列化XML为实体对象(这里实际上是新建了一个对象来反序列化)
	 * 
	 * @param obj
	 * @return boolean
	 */
	public BaseModel pasal(String json){
		BaseModel retModal = null;
		try{
			retModal = (BaseModel)JSONSerializeHelper.parseObject(json, this.getClass());
		}catch(Exception e){
			e.printStackTrace();
			retModal = null;
		}
		return retModal;
	}
	
	/**
	 * 返回当前实体对象的xml序列化信息
	 * 
	 * @return String
	 */	
	public String toString(){
		return JSONSerializeHelper.toJSON(this);
	}
}