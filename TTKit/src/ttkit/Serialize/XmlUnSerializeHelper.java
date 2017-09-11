package ttkit.Serialize;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlSerializer;
import android.util.Xml;

public class XmlUnSerializeHelper {
	
	private static final String ENCODING = "utf-8";
	
	/**
	 * 对象转换为xml
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public String beanToXml(Object obj){
		String retXml = "";
		try{
			StringWriter writer = new StringWriter();
			XmlSerializer serializer = Xml.newSerializer();
	
			serializer.setOutput(writer);
			serializer.startDocument(ENCODING, true);
			serializer.startTag(null, obj.getClass().getSimpleName());
			
			serializeObject(obj, serializer);
	
			serializer.endTag(null, obj.getClass().getSimpleName());
			serializer.endDocument();
			
			retXml = writer.toString();
			writer.flush();
			writer.close();						
		}catch(Exception e){
			e.printStackTrace();
			retXml = "";
		}

		return retXml;
	}

	/** 
     * 序列化对象
     * @param obj javaBin   
     * @param serializer   
     * @return 
     */
	private void serializeObject(Object obj,XmlSerializer serializer){
		if(null == obj)
			return;
		
		//Field[] fields = obj.getClass().getDeclaredFields();	//可访问私有属性，但访问不到父类属性；
		Field[] fields = obj.getClass().getFields();			//只能访问公有属性，但能访问父类属性；
		for (Field fd : fields) {
			boolean accessFlag = fd.isAccessible();
			// 重新设置原来的访问控制权限			
			fd.setAccessible(true);				
			try{
				serializeField(obj,serializer,fd);				
			}catch(Exception e){
				e.printStackTrace();
			}
			//恢复原来的访问控制权限
			fd.setAccessible(accessFlag);		
		}		
	}
	
	

	/** 
     * 序列化列表
     * @param serializer   
     * @param Field  
     * @param type   
     * @return 
     */
	private void serializeList(XmlSerializer serializer,Field fd, List<?> list)throws Exception{
		if(list == null){
			return;
		}		

		Type genType = fd.getGenericType();
		Class<?> subClazz = (Class<?>) ((ParameterizedType) genType).getActualTypeArguments()[0];
		Field[] subFields = subClazz.getDeclaredFields();

		for (int i = 0; i < list.size(); i++) {
			serializer.startTag(null, subClazz.getSimpleName().trim());
			
			Object subObject = list.get(i);			
			for (Field sfd : subFields) {
				serializeField(subObject,serializer,sfd);				
			}
			
			serializer.endTag(null, subClazz.getSimpleName().trim());
		}			
	}
	
	
    /** 
     * 序列化对象属性值
     * @param obj 
     * @param serializer
     * @param fd
     * @return 
     */	
	private void serializeField(Object obj,XmlSerializer serializer,Field fd)throws Exception {
		if(fd.getName().equals("this$0")){
			//非静态内部类和非静态方法中的匿名类会隐含有一个指向所在外部类实例的 this$0 属性(setAccessible为true时会访问到)
			return;
		}		
		
		serializer.startTag(null, fd.getName());		
		Class<?> type = fd.getType();				
		
		if (isBasicType(type)) { //基本类型
			setSerializerText(obj, serializer, fd, type);
		}else if(isList(type)){	  //列表
			List<?> list = (List<?>) fd.get(obj);
			serializeList(serializer, fd, list);
		}else if(isCustomized(type)){ //自定义对象
			Object fieldVal = fd.get(obj);			
			serializeObject(fieldVal,serializer);
		}else{
			;//如果是map,集合,数组则不用处理(暂不支持)
		}
		
		serializer.endTag(null, fd.getName());
	}	
 
	
    /** 
     * 判断是否是自定义对象
     * @param clazz 
     * @return 
     */  
	private boolean isCustomized(Class<?> clazz) {  
        return clazz != null && !List.class.isAssignableFrom(clazz)  
        		&& !Collection.class.isAssignableFrom(clazz) && !Map.class.isAssignableFrom(clazz) && !clazz.isArray();  
    }
	
       
    /** 
     * 判断是否是列表  
     * @param clazz 
     * @return 
     */  
	private boolean isList(Class<?> clazz) {  
        return clazz != null && List.class.isAssignableFrom(clazz) && !isCustomized(clazz);  
    }
	
	
	/**
	 * 判断是否系统基本类型
	 * 
	 * @param clazz
	 * @return
	 */
	private boolean isBasicType(Class<?> clazz) {		
		return (clazz == String.class || clazz == Integer.TYPE
				|| clazz == Float.TYPE || clazz == Double.TYPE
				|| clazz == Long.TYPE || clazz == Short.TYPE
				|| clazz == Boolean.TYPE);
	}

	/**
	 * 设置标签的文本值
	 * 
	 * @param obj
	 * @param serializer
	 * @param fd
	 * @param type
	 * @throws IOException
	 * @throws IllegalAccessException
	 */
	private void setSerializerText(Object obj, XmlSerializer serializer,
			Field fd, Class<?> type) {
		try{
			if (type == String.class) {
				serializer.text((String)fd.get(obj) == null ? "null": (String)fd.get(obj));
			} else if (type == Integer.TYPE) {
				serializer.text(String.valueOf(fd.getInt(obj)));
			} else if (type == Float.TYPE) {
				serializer.text(String.valueOf(fd.getFloat(obj)));
			} else if (type == Double.TYPE) {
				serializer.text(String.valueOf(fd.getDouble(obj)));
			} else if (type == Long.TYPE) {
				serializer.text(String.valueOf(fd.getLong(obj)));
			} else if (type == Short.TYPE) {
				serializer.text(String.valueOf(fd.getShort(obj)));
			} else if (type == Boolean.TYPE) {
				serializer.text(String.valueOf(fd.getBoolean(obj)));
			} else {
				//Log.e("setSerializerText", fd.get(obj));
				//serializer.text(fd.get(obj).toString());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
