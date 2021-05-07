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
package org.oneboot.core.mvc.file;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.oneboot.core.logging.ToString;

/**
 * @param <T>
 * @author shiqiao.pro
 * 
 */
public class ExcelObject<T extends Object> extends ToString {

	/**  */
	private static final long serialVersionUID = 1L;

	/** 导出时的文件名 不带后缀 */
	private String filename;

	/** 标题,请有序放入 */
	private Map<String, String> titles = new LinkedHashMap<>();

	/** 内容的集合 */
	private List<T> datas;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addTitle(String key, String value) {
		titles.put(key, value);
	}

	/**
	 * @param filename
	 */
	public ExcelObject(String filename) {
		super();
		this.filename = filename;
	}

	/**
	 * Getter method for property <tt>filename</tt>.
	 * 
	 * @return property value of filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Setter method for property <tt>filename</tt>.
	 * 
	 * @param filename
	 *            value to be assigned to property filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * Getter method for property <tt>titles</tt>.
	 * 
	 * @return property value of titles
	 */
	public Map<String, String> getTitles() {
		return titles;
	}

	/**
	 * Setter method for property <tt>titles</tt>.
	 * 
	 * @param titles
	 *            value to be assigned to property titles
	 */
	public void setTitles(Map<String, String> titles) {
		this.titles = titles;
	}

	/**
	 * Getter method for property <tt>datas</tt>.
	 * 
	 * @return property value of datas
	 */
	public List<T> getDatas() {
		if (datas == null) {
			datas = new ArrayList<>();
		}
		return datas;
	}

	/**
	 * Setter method for property <tt>datas</tt>.
	 * 
	 * @param datas
	 *            value to be assigned to property datas
	 */
	public void setDatas(List<T> datas) {
		this.datas = datas;
	}

}
