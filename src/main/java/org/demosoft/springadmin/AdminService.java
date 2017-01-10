package org.demosoft.springadmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Andrii_Korkoshko on 1/6/2017.
 */
@Component
public class AdminService {

    @Autowired
    private ApplicationContext applicationContext;

    public List<String> listBensNames() {
        return Arrays.asList(applicationContext.getBeanDefinitionNames());
    }

    public Object getBeanByName(String name) {
        return applicationContext.getBean(name);
    }

    public List<PropertyDescriptor> getFields(String name) {
        List<PropertyDescriptor> result = new ArrayList<>();
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(getBeanByName(name).getClass()).getPropertyDescriptors()) {
                if (pd.getReadMethod() != null && pd.getWriteMethod() != null && !"class".equals(pd.getName()))
                    result.add(pd);
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Object getValue(String beanName, PropertyDescriptor property) throws InvocationTargetException, IllegalAccessException {
        return property.getReadMethod().invoke(getBeanByName(beanName));
    }

    public void setValue(String beanName, PropertyDescriptor property, Object value) throws InvocationTargetException, IllegalAccessException {
        property.getWriteMethod().invoke(getBeanByName(beanName), value);
    }
}
