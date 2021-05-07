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
package org.oneboot.core.mybatis.extend;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author shiqiao.pro
 * 
 */
public class FieldExtMapUtil {

    /**
     * 从扩展字段读取数据
     * 
     * @param source
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void readExtMap(Object source)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (source == null) {
            return;
        }
        Class<?> cls = source.getClass();
        boolean flag = cls.isAnnotationPresent(FieldExtMap.class);
        if (flag) {
        	//Method[] methods = cls.getDeclaredMethods();  
        	//拿到所有的方法，包括父类的
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(cls);
            FieldExtMap fieldExt = cls.getAnnotation(FieldExtMap.class);

            Method extMapMethod = getFieldGetMethod(methods, fieldExt.field());
            if (extMapMethod == null) {
                // 如果get方法没有，直接返回
                return;
            }
            Object extMapValue = extMapMethod.invoke(source);
            JSONObject extObj = new JSONObject();
            if (extMapValue != null) {
                extObj = JSONObject.parseObject(extMapValue.toString());
            }

            if (extObj == null) {
                return;
            }

            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                ExtMap extMap = field.getAnnotation(ExtMap.class);
                if (extMap == null) {
                    continue;
                }

                Object value = extObj.get(field.getName());
                if (value == null) {
                    continue;
                }

                Method setMethod = getFieldSetMethod(methods, field.getName());
                if (setMethod == null) {
                    continue;
                }

                // 这个地方呀，需要做类型转换才行的，
                setMethod.invoke(source, getExtMapValue(setMethod, value));

            }

        }

    }

    private static Object getExtMapValue(Method setMethod, Object value) {
        // 得到setter方法的参数
        Type[] ts = setMethod.getGenericParameterTypes();
        // 只要一个参数
        String xclass = ts[0].toString();
        // 判断参数类型
        if ("class java.util.Date".equals(xclass)) {
            return new Date((long) value);
        }
        return value;
    }

    /**
     * 从Bean中把数据写入到ExtMap扩展字段中
     * 
     * @param source
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void outExtMap(Object source)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (source == null) {
            return;
        }
        Class<?> cls = source.getClass();
        boolean flag = cls.isAnnotationPresent(FieldExtMap.class);
        if (flag) {
            //Method[] methods = cls.getDeclaredMethods();  
        	//拿到所有的方法，包括父类的
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(cls);
            FieldExtMap fieldExt = cls.getAnnotation(FieldExtMap.class);

            Method extMapMethod = getFieldGetMethod(methods, fieldExt.field());
            if (extMapMethod == null) {
                // 如果get方法没有，直接返回
                return;
            }
            Object extMapValue = extMapMethod.invoke(source);
            JSONObject extObj = new JSONObject();
            if (extMapValue != null) {
                extObj = JSONObject.parseObject(extMapValue.toString());
            }

            Field[] fields = cls.getDeclaredFields(); //父类的还需要拿到么？不了，只管自己的？
            for (Field field : fields) {
                ExtMap extMap = field.getAnnotation(ExtMap.class);
                if (extMap == null) {
                    continue;
                }

                Method getMethod = getFieldGetMethod(methods, field.getName());
                if (getMethod == null) {
                    continue;
                }

                Object value = getMethod.invoke(source);
                if (value != null) {
                    extObj.put(field.getName(), value);
                } else {
                    // 如果获取不到值，就把key删除掉
                    extObj.remove(field.getName());
                }

            }
            Method extMapSetMethod = getFieldSetMethod(methods, fieldExt.field());
            if (extMapSetMethod != null) {
                // 把extMap数据，写入到
                extMapSetMethod.invoke(source, extObj.toJSONString());
            }

        }

    }

    /**
     * 获取字段的get方法
     * 
     * @param methods
     * @param fieldName
     * @return
     */
    private static Method getFieldGetMethod(Method[] methods, String fieldName) {
        String getName = "get" + getFirstUpperCase(fieldName);
        for (Method method : methods) {
            if (StringUtils.equals(getName, method.getName())) {
                return method;
            }
        }
        return null;
    }

    /**
     * 获取字段的get方法
     * 
     * @param methods
     * @param fieldName
     * @return
     */
    private static Method getFieldSetMethod(Method[] methods, String fieldName) {
        String getName = "set" + getFirstUpperCase(fieldName);
        for (Method method : methods) {
            if (StringUtils.equals(getName, method.getName())) {
                return method;
            }
        }
        return null;
    }

    /**
     * 首字母大写
     * 
     * @param name
     * @return
     */
    private static String getFirstUpperCase(String name) {
        String first = name.substring(0, 1).toUpperCase();
        String rest = name.substring(1, name.length());
        String newStr = new StringBuffer(first).append(rest).toString();
        return newStr;
    }

}
