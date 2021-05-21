/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oneboot.core.mybatis.base;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.oneboot.core.logging.LoggerUtil;
import org.oneboot.core.mybatis.extend.FieldExtMapUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * 
 * @author tushiqiao
 * @version $Id: AbstractRepositoryImpl.java, v 0.1 2017年5月20日 下午5:24:46 tushiqiao Exp $
 */
public abstract class BaseServiceRepositoryImpl {

    protected void outExtMap(Object source) {
        try {
            FieldExtMapUtil.outExtMap(source);
        } catch (Exception e) {
            LoggerUtil.error(e, "outExtMap 出错啦{}", source);
        }
    }

    protected void readExtMap(Object source) {
        try {
            FieldExtMapUtil.readExtMap(source);
        } catch (Exception e) {
            LoggerUtil.error(e, "readExtMap 出错啦{}", source);
        }
    }

    /**
     * 多租户Id
     * 
     * @return
     */
    protected String getTntInstId() {
        // String tntInstId = TNTInstIDEnum.SMARTPAY.getCode();
        String tntInstId = "SMARTDEF";
        /*
         * try { EventContext context = EventContextUtil.getEventContextFromTracer(); tntInstId =
         * context.getTntInstId(); if (StringUtils.isBlank(tntInstId)) { tntInstId = TNTInstIDEnum.SMARTPAY.getCode();
         * LoggerUtil.error(LOGGER, "租户信息从上下文中获取为空，设置默认值：${}", tntInstId); } } catch (Exception e) {
         * LoggerUtil.error(e, LOGGER, "租户信息从上下文中获取失败，设置默认值：${}", tntInstId); }
         */
        return tntInstId;
    }

    protected <T> T copyProperties(Object source, Class<T> target) {
        if (source == null) {
            return null;
        }
        T obj = BeanUtils.instantiateClass(target);
        BeanUtils.copyProperties(source, obj);
        readExtMap(obj);
        return obj;
    }

    protected <T> List<T> copyPropertiesList(List<? extends Object> source, Class<T> target) {
        List<T> list = new ArrayList<>();
        if (source != null) {
            for (Object object : source) {
                T obj = BeanUtils.instantiateClass(target);
                BeanUtils.copyProperties(object, obj);
                readExtMap(obj);
                list.add(obj);
            }
        }
        return list;
    }

    protected <T> List<T> copyPropertiesList(List<? extends Object> source, Class<T> target, String idName) {
        List<T> list = new ArrayList<>();
        if (source != null) {
            for (Object object : source) {
                T obj = BeanUtils.instantiateClass(target);

                Object voId = getProperty(object, idName);
                setProperty(obj, "id", voId);
                BeanUtils.copyProperties(object, obj);
                warpTheVO(object, obj);
                list.add(obj);
            }
        }
        return list;
    }

    protected void warpTheVO(Object source, Object vo) {

    }

    protected Object getProperty(Object beanObj, String property) {
        // 此处应该判断beanObj,property不为null
        try {
            PropertyDescriptor pd = new PropertyDescriptor(property, beanObj.getClass());
            Method getMethod = pd.getReadMethod();
            if (getMethod == null) {
                return null;
            }
            return getMethod.invoke(beanObj);
        } catch (Exception e) {
        }
        return null;
    }

    /* 该方法用于传入某实例对象以及对象方法名、修改值，通过放射调用该对象的某个set方法设置修改值 */
    protected void setProperty(Object beanObj, String property, Object value) {
        // 此处应该判断beanObj,property不为null
        try {
            PropertyDescriptor pd = new PropertyDescriptor(property, beanObj.getClass());
            Method setMethod = pd.getWriteMethod();
            if (setMethod == null) {

            }
            setMethod.invoke(beanObj, value);
        } catch (Exception e) {
        }
    }

    /**
     * 针对null字段不copy
     * 
     * @param source
     * @param target
     * @param ignoreProperties
     * @throws BeansException
     */
    public void copyProperties(Object source, Object target, String... ignoreProperties) throws BeansException {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null && ClassUtils.isAssignable(writeMethod.getParameterTypes()[0],
                            readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }

                            /*
                             * if (readMethod.getReturnType() == String.class) { if (value != null) {
                             * writeMethod.invoke(target, value); } } else { writeMethod.invoke(target, value); }
                             */
                            if (value != null) {
                                writeMethod.invoke(target, value);
                            }

                        } catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }

}
