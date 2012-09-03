package com.bruce.mvc;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

import com.bruce.util.BeanHelper;
import com.bruce.util.GeneralHelper;

/** {@link FormBean} 注解解析器 */
class FormBeanParser
{
	/** 装配 Form Bean */
	static final <T> void parse(Action action, Map<String, T> properties)
	{
		Class<? extends Action> clazz = action.getClass();
		FormBean fb = clazz.getAnnotation(FormBean.class);
		
		if(fb != null)
		{
			String attr = fb.value();
			
			if(GeneralHelper.isStrEmpty(attr))
				BeanHelper.setProperties(action, properties);
			else
			{
				Class<?> stopClass = action instanceof ActionSupport ? ActionSupport.class : Object.class;
				PropertyDescriptor pd = BeanHelper.getPropDescByName(clazz, stopClass, attr);
				
				if(pd != null)
				{
					Method method = pd.getWriteMethod();
					if(method != null && BeanHelper.isPublicInstanceMethod(method))
					{
						Object bean = BeanHelper.createBean(pd.getPropertyType(), properties);
						BeanHelper.setProperty(action, pd, bean);
					}
				}
			}
		}
	}
}
