package ttkit.Serialize;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

/* 功能：实现XML->Object的转换;  数据到模型;* 
 * 使用要求： 1.模型类需要和XML标签匹配；(类名，属性,通过访问属性填充值,属性必须为public)
 *        2.当前解析是根据XML层级关系解析的，对要解析列表的，需要使用list或Objct属性,如下:
 *        
 *        目前只支持object，list，基本数据类型解析；原理是解析过程中同实例类的属性做匹配，通过反射实现；
 *********************************************************************
 *  XML:
 *      <content>
  			<bookList>
	        	<book>
		        	<name>...</name>
		        	...
		        	<comment>
		        	     <text>..</text>
		        		 ...
		        	</comment>
		        </book>
		        ...
		    </bookList>
		    <ps>..</ps>
		    ...
		</content>
**********************************************************************		
*以上是一个有3级节点的Xml,每级的NodeList直接转换成List属性即可，写法如下;(内部类名需要同xml标签匹配)
***********************************************************************
 Modal类:
 		public class content{
 				public class comment{
 					private string text;
 					...//更多属性 					 
 				} 				
 				public class book{
 					private String name;
 					private List<comment> comments;
 					...//更多属性 					
 				} 				
 				private String ps;
 				private List<book> books; 				
				...//更多属性					  				
 		}
 */


/**
 *  SAX解析方式解析XML文件序列成对象(XmlToObject)
 *  @author xfy
 */
public class XmSerializeHelper extends DefaultHandler {
        private Object javaBean = null;
        private String tag; // 标签名称
        private StringBuffer sb = new StringBuffer();
        private List<String> openTagList = new ArrayList<String>();						//解析过程中打开的层级标签(这个层级只记录javaBean的层级)
        private Map<String,List<Object>> tagMap = new HashMap<String,List<Object>>();	//javaBean对象中的List和Obj属性名
        private Map<String,Class<?>>  tagClassMap = new HashMap<String,Class<?>>();		//javaBean对象中的List和Obj属性对应该的Class类型(便于创建)
                        
                       
        /**
         * 用 SAX 解析xml文件
         * 
         * @param xmlContent
         *            需要解析的Xml列表
         * @param javaBean
         *            实体类对象
         * @return Object 返回模型的实体类
         * @throws Exception
         */
        public Object xmlToBean(String xmlContent, Object javaBean)
                        throws Exception {
        		if(null == javaBean){
        			return null;
        		}
        			
                this.javaBean = javaBean; 
                //获取对象list或Obj属性的类名列表
                getListOrObjTagByBean(javaBean.getClass());
                
System.out.println(tagMap.size()+">>" + tagMap.toString());                
                
                SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                // 取得SAXParser 实例
                SAXParser parser = parserFactory.newSAXParser();
                parser.parse(new ByteArrayInputStream(xmlContent.getBytes("UTF-8")), this);                      		
                return javaBean;
        }
        /**
         * 用 SAX 解析xml文件
         * 
         * @param inputStream
         *            需要解析的Xml输入流
         * @param javaBean
         *            实体类对象
         * @return Object 返回模型的实体类
         * @throws Exception
         */
        public Object xmlToBean(InputStream inputStream, Object javaBean)
                        throws Exception {
                this.javaBean = javaBean;                               
                //获取对象list或Obj属性的类名列表
                getListOrObjTagByBean(javaBean.getClass());
                
                SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                // 取得SAXParser 实例
                SAXParser parser = parserFactory.newSAXParser();
                parser.parse(inputStream, this);                      		
                return javaBean;
        }                
        
        
        /** 
         * 检查是否有list和Object属性，有则保存tagName,在解析时通过反射填充；eg:java.util.List<cn.com.test.Plant>
         * @param clazz
         * 		       类型	 
         * @return 
         */         
        private void getListOrObjTagByBean(Class<?> clazz){
            Field[] fields = clazz.getFields();                 
            for (Field fd : fields) {               	
        		Class<?> type = fd.getType();  
        		if(isList(type)){	  //列表
        			Type genType = fd.getGenericType();
        			Class<?> subClazz = (Class<?>) ((ParameterizedType) genType).getActualTypeArguments()[0];        			        			        			        			
        			tagMap.put(subClazz.getSimpleName(), new ArrayList<Object>());
        			tagClassMap.put(subClazz.getSimpleName(), subClazz);
        			
        			//列表对象里可能还有list或obj，这里再第归一下;
        			getListOrObjTagByBean(subClazz);        			
        		}else if(isCustomized(type)){ //自定义对象
        			tagMap.put(type.getSimpleName(), new ArrayList<Object>());
        			tagClassMap.put(type.getSimpleName(), type);        			       			
        			
        			//对象里可能还有list或obj，这里再第归一下;
        			getListOrObjTagByBean(type);
        		} 
            }        	
        }        
        
        
        /** 
         * 根据类型创建对象(支持嵌套类或简单类)
         * @param clazz
         * 		       类型	 
         * @return 
         */         
        private Object createObjectByClass(Class<?> subClazz){			
			Object item = null;
			boolean  isSubClass = subClazz.getName().contains("$");//是否嵌套类;
			try{				
    			if(isSubClass){//内部类构建(内部类通过反射构建需要先创建一个主类)
    				String itemClassName = subClazz.getName();
					String mainClassName = itemClassName.substring(0, itemClassName.indexOf("$"));	        				
    				item = Class.forName(itemClassName).getDeclaredConstructors()[0].newInstance(Class.forName(mainClassName).newInstance());
    			}else{
    				item = Class.forName(subClazz.getName()).newInstance();
    			}        			        			
			}catch(Exception e){
				e.printStackTrace();
				
				Log.e("createObjectByClass", subClazz.getClass().getName());
			}
			return item;
        }
        
        
        /** 
         * 判断是否是自定义对象
         * @param clazz 
         * @return 
         */  
    	private boolean isCustomized(Class<?> clazz) {  
            return clazz != null && !List.class.isAssignableFrom(clazz)  
            		&& !Collection.class.isAssignableFrom(clazz) 
            		&& !Map.class.isAssignableFrom(clazz) 
            		&& !clazz.isArray() && !isBasicType(clazz);  
        }
    	
           
        /** 
         * 判断是否是列表  
         * @param clazz 
         * @return 
         */  
    	private boolean isList(Class<?> clazz) {  
            return clazz != null && List.class.isAssignableFrom(clazz);  
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
    	 * 根据标签名创建对应的节点对象
    	 * 
    	 * @param tagName
    	 * @return
    	 */    	
        private void createObjByTag(String tagName){
        	Class<?> clazz = tagClassMap.get(tagName);
        	List<Object> list = tagMap.get(tagName);
     
        	Object item = createObjectByClass(clazz);
			list.add(item);
        }
                
        

    	/**
    	 * （关闭子标签对应该的属性）父标签解析完后，对应的子标签列表和对象需要重新创建；
    	 * 
    	 * @param obj
    	 * @return
    	 */         
        private void endChildTag(Object obj){
            Field[] fields = obj.getClass().getFields();                 
            for (Field fd : fields) {               	
        		Class<?> type = fd.getType();			        		
        		if(isList(type)){	  //列表
        			Type genType = fd.getGenericType();
        			Class<?> subClazz = (Class<?>) ((ParameterizedType) genType).getActualTypeArguments()[0];
        			List<Object> list = tagMap.remove(subClazz.getSimpleName());
        			if(list != null){       				
        				setFieldObjectValue(obj, fd,list);
        				//父标签解析完关闭后，子标签对应的列表要重新建立一个;
        				tagMap.put(subClazz.getSimpleName(), new ArrayList<Object>());        					
        			}
        		}else if(isCustomized(type)){ //自定义对象
        			List<Object> list = tagMap.remove(type.getSimpleName());
        			if(list != null){
        				if(list.size() > 0){       					
        					setFieldObjectValue(obj, fd,list.get(list.size()-1));
        				}       				        				        			
        				//父标签解析完关闭后，子标签对应的列表要重新建立一个;
        				tagMap.put(type.getSimpleName(), new ArrayList<Object>());        					
        			}       			        		
        		}
            }
        }
        
        
    	/**
    	 * tag解析结束，将泛型对象填充到属性中(关闭对应子标签时需要创建新对象)
    	 * 
    	 * @param clazz
    	 * @return
    	 */    	
        private void endTag(Object obj,String tagName){
            Field[] fields = obj.getClass().getFields();                 
            for (Field fd : fields) {               	
        		Class<?> type = fd.getType();
	        	try{
	        		if(isList(type)){	  //列表
	        			Type genType = fd.getGenericType();
	        			Class<?> subClazz = (Class<?>) ((ParameterizedType) genType).getActualTypeArguments()[0]; 
	        			if(subClazz.getSimpleName().equals(tagName)){
	            			List<Object> list = tagMap.get(tagName);
	            			if(list != null){
	            				if(list.size() > 0){
	            					endChildTag(list.get(list.size()-1));//关闭对应的子标签
	            				}              				
	            				setFieldObjectValue(obj, fd,list);
	            			}            			
	            			return;//找到后结束
	        			}else{
	        				List<?> list = (List<?>) fd.get(obj);
	        				if(null == list){
	        					list = tagMap.get(subClazz.getSimpleName());
	        				}
	        				
	    					if(list != null){
	    						if(list.size() > 0){
	    							endTag(list.get(list.size()-1),tagName);	//列表递归最后一个对象
	    						}
	    						setFieldObjectValue(obj, fd,list);
	    					}        				
	        			}
	        		}else if(isCustomized(type)){ //自定义对象       				
	        				if(type.getSimpleName().equals(tagName)){        								
	        					List<Object> list = tagMap.get(tagName);
	                			if(list != null){
	                				if(list.size() > 0){
	                					endChildTag(list.get(list.size()-1));//关闭对应的子标签
	                					
	                					setFieldObjectValue(obj, fd,list.get(list.size()-1));
	                				}          				            				           				                				
	                			}
	                			return;//找到后结束
	        				}else{       					
	        					Object fieldVal = fd.get(obj);
	        					if(null == fieldVal){//为空时从map里根据类型查找对应的属性，找到后填入到属性中
	        						List<Object> list = tagMap.get(type.getSimpleName());
	        						fieldVal = (null != list && list.size() > 0) ? list.get(list.size() -1) :null;        						        						
	        						if(null != fieldVal){  
	                					setFieldObjectValue(obj, fd,list.get(list.size()-1));
	                				}        						
	        					}
	        					
	        					if(fieldVal != null){        						
	        						endTag(fieldVal,tagName);	//递归遍历
	        					}
	        				}
	        		} 
	           
	            }catch(Exception e){
	            	e.printStackTrace();
	            }
            }//for end
        }
                                        
        @Override
        public void startDocument() throws SAXException {        	
        }        
        
        @Override
        public void startElement(String uri, String localName, String qName,
                        Attributes attributes) throws SAXException {
        		sb.delete(0, sb.length());
        	
                tag = qName;                
                List<Object> list = tagMap.get(qName);
                if(list != null){               	
                    openTagList.add(qName);
                    
                    createObjByTag(qName);
                }
        }

        @Override
        public void characters(char[] ch, int start, int length)
                        throws SAXException {
                if (null != tag && !"".equals(tag)) {
                	sb.append(new String(ch, start, length));   	                	
                }
        }        
        
        @Override
        public void endDocument() throws SAXException {
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                        throws SAXException {
	       	 String openTag = qName;               	                	
	         List<Object> list = tagMap.get(openTag);                     
	         if(list != null){//解析带子节点的标签节点
	         	if(list.size() == 0){
	         		return;
	         	}                    	
	           	
	         	Object item = list.get(list.size()-1);
	         	Field[] fields = item.getClass().getFields();
	             for (Field fd : fields) {
	                     if (openTag.equalsIgnoreCase(fd.getName())) {	                                        	
	                     		setFieldValuee(item, fd, sb.toString());
	                     }
	             }                    	
	         }else{
	         	Field[] fields = javaBean.getClass().getFields();
	             for (Field fd : fields) {
	                     if (openTag.equalsIgnoreCase(fd.getName())) {	                                        	
	                     		setFieldValuee(javaBean,fd, sb.toString());
	                     }
	             }                     	
	         }        
        	
        	tag = null;//必须要置空;	 
            if(list != null){
            	openTagList.remove(qName);
            	
            	endTag(javaBean,qName);           	
            }        	   	       
        }
  
        /**
         * 直接设置属性值(对象,主要用于对象嵌套)
         * 
         * @param object
         *            类
         * @param setName
         *            方法名
         * @param setValue方法设置
         * @param obj
         *            属性类型
         * @throws Exception
         */         
        private void setFieldObjectValue(Object object, Field field, Object setValue) {
    		try {
    			field.set(object, setValue);
    		} catch (IllegalAccessException e) {
    			e.printStackTrace();
    		} catch (IllegalArgumentException e) {
    			e.printStackTrace();
    		}        	
        }
        
        
        /**
         * 直接设置属性值(基本类型)
         * 
         * @param object
         *            类
         * @param field
         *            属性对象
         * @param setValue
         * 			     方法设置
         * @throws Exception
         */        
		private void setFieldValuee(Object object, Field field, String setValue) {
    		//枚举转换
    		try {   	
    				Class<?> obj = field.getType();
	    			// 基本类型转换
	    			if (obj.equals(Integer.class) || obj.equals(int.class)) {
	    				field.set(object, Integer.valueOf(setValue));
		            }
		            if (obj.equals(Float.class) || obj.equals(float.class)) {
		            	field.set(object, Float.valueOf(setValue));
		            }
		            if (obj.equals(Short.class) || obj.equals(short.class)) {
		            	field.set(object, Short.valueOf(setValue));
		            }
		            if (obj.equals(Byte.class) || obj.equals(byte.class)) {
		            	field.set(object, Byte.valueOf(setValue));
		            }
		            if (obj.equals(Double.class) || obj.equals(double.class)) {
		            	field.set(object, Double.valueOf(setValue));
		            }
		            if (obj.equals(Date.class)) {
		            	field.set(object, Date.valueOf(setValue));
		            }
		            if (obj.equals(Long.class) || obj.equals(long.class)) {
		            	field.set(object, Long.valueOf(setValue));
		            }
		            if (obj.equals(Boolean.class)
		                            || obj.equals(boolean.class)) {
		            	field.set(object, Boolean.valueOf(setValue));
		            }
		            if (obj.equals(String.class)) {
		            	field.set(object, setValue);
		            }  
            } catch (Exception e) {
                e.printStackTrace();
            }
    	}

}