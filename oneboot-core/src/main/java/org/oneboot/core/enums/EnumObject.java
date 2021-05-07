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
package org.oneboot.core.enums;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.oneboot.core.utils.PinyinFormatUtil;

import lombok.Getter;
import lombok.Setter;

/**
 * Enum转对像
 * 
 * @author shiqiao.pro
 * 
 */
@Getter
@Setter
public class EnumObject implements Serializable {

	/** serialVersionUID **/
	private static final long serialVersionUID = -7875517333873324940L;

	/** value */
	private String value;

	/** 显示名字 */
	private String name;

	/** 显示名字 */
	private String label;

	/** 中文拼音 */
	private String pinyin;

	private List<EnumObject> children;

	private boolean disabled = false;

	public String getLabel() {
		if (StringUtils.isBlank(label)) {
			return name;
		}
		return label;
	}

	public List<EnumObject> getChildren() {
		if (children != null && children.isEmpty()) {
			return null;
		}
		return children;
	}

	public String getPinyin() {
		if (StringUtils.isBlank(pinyin)) {
			return name + PinyinFormatUtil.formatPinYinStr(name);
		}
		return name + pinyin;
	}

	public void addChildren(EnumObject eo) {
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
		this.children.add(eo);
	}

	public void addChildren(List<EnumObject> children) {
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
		this.children.addAll(children);
	}

}
