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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.oneboot.core.lang.Page;
import org.oneboot.core.mybatis.model.BaseTotalSearcher;
import org.springframework.beans.BeanUtils;

import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 
 * @author shiqiao.pro
 * 
 */
public abstract class AbstractRepositoryImpl extends BaseServiceRepositoryImpl {
	protected <T> Page<T> copyToPage(List<? extends Object> source, Class<T> target, BaseTotalSearcher context) {
		List<T> projectList = copyPropertiesList(source, target);
		Page<T> page = new Page<T>();
		page.setPageNo(context.getPageNo());
		page.setPageSize(context.getPageSize());
		page.setTotalCount(context.getTotalCount());
		page.setResult(projectList);
		return page;
	}

	/**
	 * 分割List
	 * 
	 * @param list
	 *            待分割的list
	 * @param pageSize
	 *            每段list的大小
	 * @return List<<List<T>>
	 */
	public static <T> List<List<T>> splitList(List<T> list, int pageSize) {
		List<List<T>> listArray = new ArrayList<List<T>>();
		List<T> subList = null;
		for (int i = 0; i < list.size(); i++) {
			if (i % pageSize == 0) {
				// 每次到达页大小的边界就重新申请一个subList
				subList = new ArrayList<T>();
				listArray.add(subList);
			}
			subList.add(list.get(i));
		}

		return listArray;
	}

	protected <T> Page<T> copyToPage(IPage<? extends Object> result, Class<T> clazz) {

		List<T> projectList = copyPropertiesList(result.getRecords(), clazz);
		Page<T> page = new Page<T>();
		page.setPageNo((int) result.getCurrent());
		page.setPageSize((int) result.getSize());
		page.setTotalCount((int) result.getTotal());
		page.setResult(projectList);
		return page;

	}

	protected <D> List<D> copyOutDoList(Collection<? extends Object> source, Class<D> target) {
		List<D> list = new ArrayList<>();
		if (source != null) {
			for (Object object : source) {
				outExtMap(object);
				D obj = BeanUtils.instantiateClass(target);
				BeanUtils.copyProperties(object, obj);
				list.add(obj);
			}
		}
		return list;
	}

}
