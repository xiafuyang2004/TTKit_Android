package ttkit.model.base;

import ttkit.Serialize.XmSerializeHelper;
import ttkit.Serialize.XmlUnSerializeHelper;

/**
 * xml模型基类接口.
 * @author xfy
 *
 */
public class BaseModelXml extends BaseModel{
	/**
	 * 序列化XML为实体对象
	 * 
	 * @param obj
	 * @return boolean
	 */
	@Override
	public BaseModel pasal(String xml){
		try{
			XmSerializeHelper helper = new XmSerializeHelper();
			helper.xmlToBean(xml, this);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return this;
	}
	
	/**
	 * 返回当前实体对象的xml序列化信息
	 * 
	 * @return String
	 */	
	@Override
	public String toString(){
		XmlUnSerializeHelper helper = new XmlUnSerializeHelper();
		return helper.beanToXml(this);
	}
}