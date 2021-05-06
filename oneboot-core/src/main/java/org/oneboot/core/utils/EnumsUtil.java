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
package org.oneboot.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.oneboot.core.enums.EnumObject;
import org.oneboot.core.enums.IEnum;

/**
 * 枚举工具类
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public class EnumsUtil {

	/**
	 * 把enum封成list，便于前端调用
	 *
	 * @param clazz
	 * @param <T>
	 * @return 例如： model.addAttribute("cateList",
	 *         Enums.toList(Enums.BrandCate.class)); <select cname="type" >
	 *         <c:forEach var="type" items="${cateList}">
	 *         <option value="${type.key}">${type.cname}</option> </c:forEach>
	 *         </select>
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IEnum> List<IEnum> toList(Class<T> clazz) {
		return (List<IEnum>) Arrays.asList(clazz.getEnumConstants());
	}

	/**
	 * 把enum封成list，便于前端调用
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T extends IEnum> List<EnumObject> toEnumObject(Class<T> clazz) {
		List<IEnum> list = EnumsUtil.toList(clazz);
		List<EnumObject> eoList = new ArrayList<>();
		for (IEnum e : list) {
			EnumObject eo = new EnumObject();
			eo.setName(e.getName());
			eo.setValue(e.getCode());
			eo.setPinyin(PinyinFormatUtil.formatPinYinStr(e.getName()));
			eoList.add(eo);
		}
		return eoList;
	}

	public static Set<String> toValueList(Class<? extends IEnum> value) {
		Set<String> values = new HashSet<>();
		List<IEnum> list = Arrays.asList(value.getEnumConstants());
		for (IEnum e : list) {
			values.add(e.getCode());
		}
		return values;
	}

}
