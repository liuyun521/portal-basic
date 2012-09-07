package com.bruce.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/** Java Bean 帮助类，执行 Java Bean 属性的 get / set 相关操作 */
public class BeanHelper
{
	/** 简单数据类型集合 */
	public static final Set<Class<?>> SMIPLE_CLASS_SET	= new HashSet<Class<?>>(18);
	/** 基本类型包装类集合 */
	public static final Set<Class<?>> WRAPPER_CLASS_SET	= new HashSet<Class<?>>(8);

	private static final String STRING_DELIMITERS = " ,;\t\n\r\f";
	
	static
	{
		SMIPLE_CLASS_SET.add(int.class);
		SMIPLE_CLASS_SET.add(long.class);
		SMIPLE_CLASS_SET.add(float.class);
		SMIPLE_CLASS_SET.add(double.class);
		SMIPLE_CLASS_SET.add(byte.class);
		SMIPLE_CLASS_SET.add(char.class);
		SMIPLE_CLASS_SET.add(short.class);
		SMIPLE_CLASS_SET.add(boolean.class);
		SMIPLE_CLASS_SET.add(Integer.class);
		SMIPLE_CLASS_SET.add(Long.class);
		SMIPLE_CLASS_SET.add(Float.class);
		SMIPLE_CLASS_SET.add(Double.class);
		SMIPLE_CLASS_SET.add(Byte.class);
		SMIPLE_CLASS_SET.add(Character.class);
		SMIPLE_CLASS_SET.add(Short.class);
		SMIPLE_CLASS_SET.add(Boolean.class);
		SMIPLE_CLASS_SET.add(String.class);
		SMIPLE_CLASS_SET.add(Date.class);
		
		WRAPPER_CLASS_SET.add(Integer.class);
		WRAPPER_CLASS_SET.add(Long.class);
		WRAPPER_CLASS_SET.add(Float.class);
		WRAPPER_CLASS_SET.add(Double.class);
		WRAPPER_CLASS_SET.add(Byte.class);
		WRAPPER_CLASS_SET.add(Character.class);
		WRAPPER_CLASS_SET.add(Short.class);
		WRAPPER_CLASS_SET.add(Boolean.class);
	}

	/** 检查是否为非抽象公共实例方法 */
	public static final boolean isPublicInstanceMethod(Method method)
	{
		int flag = method.getModifiers();
		return (!Modifier.isStatic(flag) && !Modifier.isAbstract(flag) && Modifier.isPublic(flag));
	}
	
	/** 检查是否为非接口非抽象公共类 */
	public static final boolean isPublicNotAbstractClass(Class<?> clazz)
	{
		int flag = clazz.getModifiers();
		return (!Modifier.isInterface(flag) && !Modifier.isAbstract(flag) && Modifier.isPublic(flag));
	}

	/** 检查 clazz 是否为简单数据类型 */
	public final static boolean isSimpleType(Class<?> clazz)
	{
		return SMIPLE_CLASS_SET.contains(clazz);
	}

	/** 检查 clazz 是否为基础类型包装类 */
	public final static boolean isWrapperType(Class<?> clazz)
	{
		return WRAPPER_CLASS_SET.contains(clazz);
	}

	/** 检查包装类和基础类型是否匹配 */
	public final static boolean isWrapperAndPrimitiveMatch(Class<?> wrapperClazz, Class<?> primitiveClass)
	{
		if(!primitiveClass.isPrimitive())	return false;
		if(!isWrapperType(wrapperClazz))	return false;
		
		try
		{
			Field f = wrapperClazz.getDeclaredField("TYPE");
			return f.get(null) == primitiveClass;
		}
		catch(Exception e)
		{
			
		}
		
		return false;
	}
	
	/** 检查源数组的元素类型是否兼容目标数组的元素类型 */
	public static final boolean isCompatibleArray(Class<?> srcClazz,Class<?> destClazz)
	{
		if(srcClazz.isArray() && destClazz.isArray())
		{
			Class<?> srcComponentType = srcClazz.getComponentType();
			Class<?> destComponentType = destClazz.getComponentType();
		
			return	(
						destComponentType.isAssignableFrom(srcComponentType)			||
						isWrapperAndPrimitiveMatch(destComponentType, srcComponentType)	||
						isWrapperAndPrimitiveMatch(srcComponentType, destComponentType)
					);
		}
		
		return false;
	}
	
	/** 创建指定类型的 Java Bean，并设置相关属性
	 * 
	 *  @param clazz		: Bean 类型
	 *  @param properties	: 属性名 / 值映射<br>
	 *  					  其中名称为 {@link String} 类型，与属性名称一致<br>
	 *  					  属性值可能为以下 3 中类型：<br>
	 *  					  &nbsp; &nbsp; 1) 属性的实际类型：直接对属性赋值<br>
	 *  					  &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对属赋值<br>
	 *  					  &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对属赋值<br> 
	 *  @return				: 生成的 Bean实例
	 */
	public static final <B, T> B createBean(Class<B> clazz, Map<String, T> properties)
	{
		return createBean(clazz, properties, null);
	}
	
	/** 创建指定类型的 Java Bean，并设置相关属性
	 * 
	 *  @param clazz		: Bean 类型
	 *  @param properties	: 属性名 / 值映射<br>
	 *  					  其中名称为 {@link String} 类型，与属性名称可能一直也可能不一致<br>
	 *  					  属性值可能为以下 3 中类型：<br>
	 *  					  &nbsp; &nbsp; 1) 属性的实际类型：直接对属性赋值<br>
	 *  					  &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对属赋值<br>
	 *  					  &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对属赋值<br>
	 *  @param keyMap		: properties.key / Bean 属性名映射，当 properties 的 key 与属性名不对应时，
	 *  					  用 keyMap 把它们关联起来
	 *  @return				  生成的 Bean实例  
	 */
	public static final <B, T> B createBean(Class<B> clazz, Map<String, T> properties, Map<String, String> keyMap)
	{
		B bean = null;
		
		try
		{
			bean = clazz.newInstance();
			setProperties(bean, properties, keyMap);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return bean;
	}
	
	/** 设置 Java Bean 的属性
	 * 
	 *  @param bean			: Bean 实例
	 *  @param properties	: 属性名 / 值映射<br>
	 *  					  其中名称为 {@link String} 类型，与属性名称一致<br>
	 *  					  属性值可能为以下 3 中类型：<br>
	 *  					  &nbsp; &nbsp; 1) 属性的实际类型：直接对属性赋值<br>
	 *  					  &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对属赋值<br>
	 *  					  &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对属赋值<br> 
	 */
	public static final <T> void setProperties(Object bean, Map<String, T> properties)
	{
		setProperties(bean, properties, null);
	}
	
	/** 设置 Java Bean 的属性
	 * 
	 *  @param bean			: Bean 实例
	 *  @param properties	: 属性名 / 值映射<br>
	 *  					  其中名称为 {@link String} 类型，与属性名称可能一直也可能不一致<br>
	 *  					  属性值可能为以下 3 中类型：<br>
	 *  					  &nbsp; &nbsp; 1) 属性的实际类型：直接对属性赋值<br>
	 *  					  &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对属赋值<br>
	 *  					  &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对属赋值<br>
	 *  @param keyMap		: properties.key / Bean 属性名映射，当 properties 的 key 与属性名不对应时，
	 *  					  用 keyMap 把它们关联起来  
	 */
	public static final <T> void setProperties(Object bean, Map<String, T> properties, Map<String, String> keyMap)
	{
		Map<String, PropertyDescriptor> pps = getPropDescMap(bean.getClass());
		Set<Map.Entry<String, T>> set		= properties.entrySet();
		
		for(Map.Entry<String, T> o : set)
		{
			String name	= null;
			String key	= o.getKey();
			
			if(keyMap != null)
				name = keyMap.get(key);
			if(name == null)
				name = key;

			T value	= o.getValue();
			PropertyDescriptor pd = pps.get(name);
			
			if(pd != null && value != null)
				setProperty(bean, pd, value);
		}
	}

	/** 设置 Java Bean 的属性
	 * 
	 *  @param bean		: Bean 实例
	 *  @param pd		: 属性描述符<br>
	 *  @param value	: 属性值，可能为以下 3 中类型：<br>
	 *  					  &nbsp; &nbsp; 1) 属性的实际类型：直接对属性赋值<br>
	 *  					  &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对属赋值<br>
	 *  					  &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对属赋值<br>  
	 */
	@SuppressWarnings("unchecked")
	public static final <T> boolean setProperty(Object bean, PropertyDescriptor pd, T value)
	{
		boolean isOK	= false;
		Method method 	= pd.getWriteMethod();
		
		if(method != null && isPublicInstanceMethod(method))
		{
			method.setAccessible(true);
			
			Class<?> clazz = pd.getPropertyType();

			if(value == null || clazz.isAssignableFrom(value.getClass()))
				isOK = tryInvokeMethod(bean, method, value);
			else if(isWrapperAndPrimitiveMatch(value.getClass(), clazz))
				isOK = tryInvokeMethod(bean, method, value);
			else if(isCompatibleArray(value.getClass(), clazz))
				isOK = setArrayProperty(bean, value, method, clazz.getComponentType());
			else if(Collection.class.isAssignableFrom(clazz))
				isOK = setCollectionProperty(bean, value, method, (Class<? extends Collection<?>>)clazz, pd);
			else
				isOK = setProperty(bean, value, method, clazz);
		}
		
		return isOK;
	}
	
	private static final <T> boolean setArrayProperty(Object bean, T value, Method method, Class<?> clazz)
	{
		int length = Array.getLength(value);
		Object array = Array.newInstance(clazz, length);
		
		for(int i = 0;i < length; i++)
			Array.set(array, i, Array.get(value, i));
		
		return tryInvokeMethod(bean, method, array);
	}

	private static final <T> boolean setCollectionProperty(Object bean, T value, Method method, Class<? extends Collection<?>> colClazz, PropertyDescriptor pd)
	{
		boolean isOK	= false;
		Field field		= getFiledByName(bean.getClass(), pd.getName());
		
		if(field != null)
		{
			Type genericType = field.getGenericType();
			if(genericType instanceof ParameterizedType)
			{
				Class<?> paramClazz = (Class<?>)(((ParameterizedType)genericType).getActualTypeArguments()[0]);
				isOK = setCollectionProperty(bean, value, method, colClazz, paramClazz);
			}
		}
		
		return isOK;
	}

	private static final <T> boolean setCollectionProperty(Object bean, T value, Method method, Class<? extends Collection<?>> clazz, Class<?> paramClazz)
	{
		Class<?> valueType		= value.getClass();
		Class<?> valueComType	= valueType.getComponentType();
		
		if	(
				isSimpleType(paramClazz)					&&
				(	
					(valueType.equals(String.class))		|| 
					(valueType.isArray() && valueComType.equals(String.class))
				)

			)
		{
			Collection<?> col = parseCollectionParameter(clazz, paramClazz, value);
			return tryInvokeMethod(bean, method, col);
		}

		return false;
	}
	
	private static final <T> Collection<?> parseCollectionParameter(Class<? extends Collection<?>> clazz, Class<?> paramClazz, T obj)
	{
		Collection<Object> col = getRealCollectionClass(clazz);
		
		if(col != null)
		{
			Class<?> valueType	= obj.getClass();
			String[] value = null;
			
			if(valueType.isArray())
				value	= (String[])obj;
			else
			{
				String str	= (String)obj;
				StringTokenizer st = new StringTokenizer(str, STRING_DELIMITERS);
				value	= new String[st.countTokens()];

				for(int i = 0; st.hasMoreTokens(); i++)
					value[i] = st.nextToken();
			}

			for(int i = 0; i < value.length; i++)
			{
				String v = value[i];
				Object p = GeneralHelper.str2Object(paramClazz, v);
				col.add(p);
			}
		}
		

		
		return col;
	}

	@SuppressWarnings("unchecked")
	private static final Collection<Object> getRealCollectionClass(Class<? extends Collection<?>> clazz)
	{
		Collection<?> col	= null;
		Class<?> realClazz	= null;
		
		if(isPublicNotAbstractClass(clazz))
			realClazz = clazz;
		else if(Collection.class.isAssignableFrom(clazz))
			realClazz = ArrayList.class;
		else if(Set.class.isAssignableFrom(clazz))
			realClazz = HashSet.class;

		if(realClazz != null)
		{
			try
			{
				col = (Collection<?>)realClazz.newInstance();
			}
			catch(Exception e)
			{

			}
		}
		
		return (Collection<Object>)col;
	}

	private static final <T> boolean setProperty(Object bean, T value, Method method, Class<?> clazz)
	{
		Class<?> valueType		= value.getClass();
		Class<?> valueComType	= valueType.getComponentType();
		Class<?> clazzComType	= clazz.getComponentType();

		if	(
				(	
					(valueType.equals(String.class))		|| 
					(valueType.isArray() && valueComType.equals(String.class))
				)
				&&
				(	
					(isSimpleType(clazz))		|| 
					(clazz.isArray() && isSimpleType(clazzComType))
				)
			)
		{
			Object param = parseParameter(clazz, value);
			return tryInvokeMethod(bean, method, param);
		}

		return false;
	}
	
	private static final Object invokeMethod(Object bean, Method method, Object ... param)
	{		
		try
		{
			return method.invoke(bean, param);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private static final boolean tryInvokeMethod(Object bean, Method method, Object ... param)
	{
		boolean isOK = true;
		
		try
		{
			method.invoke(bean, param);
		}
		catch (Exception e)
		{
			isOK = false;
		}
		
		return isOK;
	}

	private static final <T> Object parseParameter(Class<?> clazz, T obj)
	{
		Object param		= null;
		Class<?> valueType	= obj.getClass();
		
		if(clazz.isArray())
		{
			String[] value = null;
			
			if(valueType.isArray())
				value	= (String[])obj;
			else
			{
				String str	= (String)obj;
				StringTokenizer st = new StringTokenizer(str, STRING_DELIMITERS);
				value	= new String[st.countTokens()];

				for(int i = 0; st.hasMoreTokens(); i++)
					value[i] = st.nextToken();
			}
			
			int length		= value.length;
			Class<?> type	= clazz.getComponentType();
			param			= Array.newInstance(type, length);

			for(int i = 0; i < length; i++)
			{
				String v = value[i];
				Object p = GeneralHelper.str2Object(type, v);
				Array.set(param, i, p);
			}
		}
		else
		{
			String value = null;
			
			if(valueType.isArray())
			{
				String[] array	= (String[])obj;
				if(array.length > 0)
					value = array[0];
			}
			else
				value = (String)obj;
			
			param = GeneralHelper.str2Object(clazz, value);
		}
		
		return param;
	}
	
	/** 获取指定类型 Java Bean 的所有属性描述（包括 Object 类除外的所有父类定义的属性）
	 * 
	 *  @param startClass	: Bean 类型
	 *  @return				  属性名 / 描述对象映射  
	 */
	public static final Map<String, PropertyDescriptor> getPropDescMap(Class<?> startClass)
	{
		return getPropDescMap(startClass, Object.class);
	}
	
	/** 获取指定类型 Java Bean 的所有属性描述（包括 stopClass 及更高层父类除外的所有父类定义的属性）
	 * 
	 *  @param startClass	: Bean 类型
	 *  @param stopClass	: 截止查找的父类类型
	 *  @return				  属性名 / 描述对象映射  
	 */
	public static final Map<String, PropertyDescriptor> getPropDescMap(Class<?> startClass, Class<?> stopClass)
	{
		Map<String, PropertyDescriptor> map = new HashMap<String, PropertyDescriptor>();
		
		try
		{
			BeanInfo info = Introspector.getBeanInfo(startClass, stopClass);
			PropertyDescriptor[] pds = info.getPropertyDescriptors();
			
			for(PropertyDescriptor pd : pds)
				map.put(pd.getName(), pd);
		}
		catch(IntrospectionException e)
		{
			throw new RuntimeException(e);
		}
		
		return map;
	}
	
	/** 获取 Java Bean 的属性
	 * 
	 *  @param bean	: Bean 实例
	 *  @return		: Bean 属性名 / 值映射  
	 */
	public static final Map<String, Object> getProperties(Object bean)
	{
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, PropertyDescriptor> pps = getPropDescMap(bean.getClass());
		Set<Map.Entry<String, PropertyDescriptor>> set = pps.entrySet();
		
		for(Map.Entry<String, PropertyDescriptor> o : set)
		{
			String key = o.getKey();
			PropertyDescriptor pd = o.getValue();
			Method method = pd.getReadMethod();
			
			if(method != null && isPublicInstanceMethod(method))
			{
				method.setAccessible(true);
				
				Object value = invokeMethod(bean, method);
				result.put(key, value);
			}
		}
		
		return result;
	}

	/** 获取指定类型 Java Bean 的名称为 name 的属性描述对象
	 * 
	 *  @param startClass	: Bean 类型
	 *  @param name			: 属性名称
	 *  @return				  描述对象映射，找不到属性则返回 null  
	 */
	public static final PropertyDescriptor getPropDescByName(Class<?> startClass, String name)
	{
		return getPropDescByName(startClass, Object.class, name);
	}
	
	/** 获取指定类型 Java Bean 的名称为 name 的属性描述对象
	 * 
	 *  @param startClass	: Bean 类型
	 *  @param name			: 属性名称
	 *  @param stopClass	: 截止查找的父类类型
	 *  @return				  描述对象映射，找不到属性则返回 null  
	 */
	public static final PropertyDescriptor getPropDescByName(Class<?> startClass, Class<?> stopClass, String name)
	{
		try
		{
			BeanInfo info = Introspector.getBeanInfo(startClass, stopClass);
			PropertyDescriptor[] pds = info.getPropertyDescriptors();
			
			for(PropertyDescriptor pd : pds)
			{
				if(pd.getName().equals(name))
					return pd;
			}
		}
		catch(IntrospectionException e)
		{
			throw new RuntimeException(e);
		}
		
		return null;
	}

	
	/** 设置 Java Bean 的名称为 name 的属性值
	 * 
	 *  @param bean			: Bean 实例
	 *  @param name			: 属性名称
	 *  @param value		: 属性值可能为以下 3 中类型：<br>
	 *  					  &nbsp; &nbsp; 1) 属性的实际类型：直接对属性赋值<br>
	 *  					  &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对属赋值<br>
	 *  					  &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对属赋值<br> 
	 */
	public static final <T> boolean setProperty(Object bean, String name, T value)
	{
		PropertyDescriptor pd = getPropDescByName(bean.getClass(), name);
		if(pd != null)
			return setProperty(bean, pd, value);
		
		return false;
	}
	
	/** 获取 Java Bean 的名称为 name 的属性值
	 * 
	 *  @param bean			: Bean 实例
	 *  @param name			: 属性名称
	 *  @throws				  RuntimeException 失败则抛出相应的运行期异常
	 */
	public static final <T> T getProperty(Object bean, String name)
	{
		PropertyDescriptor pd = getPropDescByName(bean.getClass(), name);
		
		if(pd != null)
			return getProperty(bean, pd);
		
		throw new RuntimeException(new IllegalArgumentException());
	}

	/** 获取 Java Bean 的名称为 name 的属性值
	 * 
	 *  @param bean			: Bean 实例
	 *  @param pd			: 属性描述符
	 *  @throws				  RuntimeException 失败则抛出相应的运行期异常
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T getProperty(Object bean, PropertyDescriptor pd)
	{
		Method method = pd.getReadMethod();
		if(method != null && isPublicInstanceMethod(method))
		{
			method.setAccessible(true);
			return (T)invokeMethod(bean, method);
		}
		
		throw new RuntimeException(new IllegalArgumentException());
	}

	/** 获取某个类所有属性的 {@link Field} 对象
	 * 
	 *  @param clazz		: 要查找的类
	 *  @return				: 失败返回空集合
	 */	
	public static final Set<Field> getAllFields(Class<?> clazz)
	{
		return getAllFields(clazz, null);
	}
	
	/** 获取某个类所有属性的 {@link Field} 对象
	 * 
	 *  @param clazz		: 要查找的类
	 *  @param stopClazz	: 终止查找的类（这个类的属性也不被获取）
	 *  @return				: 失败返回空集合
	 */
	public static final Set<Field> getAllFields(Class<?> clazz, Class<?> stopClazz)
	{
		Set<Field> fields = new HashSet<Field>();
		
		while(clazz != null && clazz != stopClazz)
		{
			Field[] f = clazz.getDeclaredFields();
			Collections.addAll(fields, f);
			
			clazz = clazz.getSuperclass();
			
		}
		
		return fields;
	}
	
	/** 获取某个类中名称为 name 的属性的 {@link Field} 对象
	 * 
	 *  @param clazz		: 要查找的类
	 *  @param name			: 方法名称
	 *  @return				: 失败返回 null
	 */
	public static final Field getFiledByName(Class<?> clazz,String name)
	{
		return getFiledByName(clazz, null, name);
	}

	/** 获取某个类中名称为 name 的属性的 {@link Field} 对象
	 * 
	 *  @param clazz		: 要查找的类
	 *  @param stopClazz	: 终止查找的类（这个类的属性也不被获取）
	 *  @param name			: 方法名称
	 *  @return				: 失败返回 null
	 */
	public static final Field getFiledByName(Class<?> clazz, Class<?> stopClazz, String name)
	{
		Field f = null;
		
		do
		{
			try
			{
				f = clazz.getDeclaredField(name);
			}
			catch(NoSuchFieldException e)
			{
				clazz = clazz.getSuperclass();
			}
		} while(f == null && clazz != null && clazz != stopClazz);
		
		return f;
	}
	
	/** 获取某个类所有方法的 {@link Method} 对象
	 * 
	 *  @param clazz		: 要查找的类
	 *  @return				: 失败返回空集合
	 */
	public static final Set<Method> getAllMethods(Class<?> clazz)
	{
		return getAllMethods(clazz, null);
	}
	
	/** 获取某个类所有方法的 {@link Method} 对象
	 * 
	 *  @param clazz		: 要查找的类
	 *  @param stopClazz	: 终止查找的类（这个类的方法也不被获取）
	 *  @return				: 失败返回空集合
	 */
	public static final Set<Method> getAllMethods(Class<?> clazz, Class<?> stopClazz)
	{
		Set<Method> methods = new HashSet<Method>();
		
		while(clazz != null && clazz != stopClazz)
		{
			Method[] m = clazz.getDeclaredMethods();
			Collections.addAll(methods, m);
			
			clazz = clazz.getSuperclass();
			
		}
		
		return methods;
	}
	
	/** 获取某个类中名称为 name，参数为 parameterTypes 的方法的 {@link Method} 对象
	 * 
	 *  @param clazz			: 要查找的类
	 *  @param name				: 方法名称
	 *  @param parameterTypes	: 参数类型
	 *  @return					: 失败返回 null
	 */
	public static final Method getMethodByName(Class<?> clazz, String name, Class<?>... parameterTypes)
	{
		return getMethodByName(clazz, null, name, parameterTypes);
	}
	
	/** 获取某个类中名称为 name，参数为 parameterTypes 的方法的 {@link Method} 对象
	 * 
	 *  @param clazz		: 要查找的类
	 *  @param stopClazz	: 终止查找的类（这个类的方法也不被获取）
	 *  @param name				: 方法名称
	 *  @param parameterTypes	: 参数类型
	 *  @return					: 失败返回 null
	 */
	public static final Method getMethodByName(Class<?> clazz, Class<?> stopClazz, String name, Class<?>... parameterTypes)
	{
		Method m = null;
		
		do
		{
			try
			{
				m = clazz.getDeclaredMethod(name, parameterTypes);
			}
			catch(NoSuchMethodException e)
			{
				clazz = clazz.getSuperclass();
			}
		} while(m == null && clazz != null && clazz != stopClazz);
		
		return m;
	}

}