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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.oneboot.core.enums.EnumObject;
import org.oneboot.core.lang.Page;
import org.oneboot.core.mybatis.model.BaseSearcher;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.Wrapper;

/**
 * 
 * @param <T>
 * @param <D>
 *            DO对像
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public interface IRepository<T extends BaseModel, D> {

	/**
	 * 返回DO实例对象
	 * 
	 * @return
	 */
	D create();

	/**
	 * 返回DO class
	 * 
	 * @return
	 */
	Class<D> getDoClazz();

	/**
	 * 返回Model calss
	 * 
	 * @return
	 */
	Class<T> getMclazz();

	/**
	 * 插入一条记录（选择字段，策略插入）
	 * 
	 * @param entity
	 *            实体对象
	 * @return
	 */
	boolean save(T entity);

	/**
	 * 插入（批量）
	 * 
	 * @param entityList
	 *            实体对象集合
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	default boolean saveBatch(Collection<T> entityList) {
		return saveBatch(entityList, 2000);
	}

	/**
	 * 插入（批量）
	 *
	 * @param entityList
	 *            实体对象集合
	 * @param batchSize
	 *            插入批次数量
	 * @return
	 */
	boolean saveBatch(Collection<T> entityList, int batchSize);

	/**
	 * 根据 ID 选择修改
	 *
	 * @param entity
	 *            实体对象
	 * @return
	 */
	boolean updateById(T entity);

	/**
	 * 根据 ID 增量修改，注意，此方法不会对ext_map字段做修复
	 * 
	 * @param entity
	 * @return
	 */
	boolean updatePatchById(T entity);

	/**
	 * 批量更新字段值
	 * 
	 * @param idList
	 *            IDs
	 * @param field
	 *            需要修改的字段
	 * @param value
	 *            修改的内容
	 * @return
	 */
	int updateBatchField(List<String> idList, String field, Object value);

	/**
	 * 根据 ID 删除
	 *
	 * @param id
	 *            主键ID
	 * @return
	 */
	boolean removeById(Object id);

	/**
	 * 根据 columnMap 条件，删除记录
	 *
	 * @param columnMap
	 *            表字段 map 对象
	 * @return
	 */
	boolean removeByMap(Map<String, Object> columnMap);

	boolean removeByMap(String column, Object value);

	/**
	 * 根据 ID 查询
	 *
	 * @param id
	 *            主键ID
	 * @return
	 */
	T findById(Object id);

	T findOne(Wrapper<D> wrapper);

	T findOne(Map<String, Object> columnMap);

	T findOne(String column, Object value);

	/**
	 * 查询所有记录
	 * 
	 * @return
	 */
	List<T> findAll();

	/**
	 * 根据ID查询列表
	 * 
	 * @param ids
	 * @return
	 */
	List<T> findAll(String... ids);

	List<T> findList(BaseSearcher bs);

	/**
	 * 获取到所有的数据，通过查询条件
	 * 
	 * @param bs
	 * @return
	 */
	List<T> findListAllPage(BaseSearcher bs);

	/**
	 * 通过 key value进行数据返回
	 * 
	 * @param column
	 * @param value
	 * @return
	 */
	List<T> findList(String column, Object value);

	List<T> findList(Map<String, Object> columnMap);

	List<T> findList(Wrapper<D> wrapper);

	/**
	 * 数据是否已经存在
	 * 
	 * @param fieldName
	 *            字段名
	 * @param value
	 * @return
	 */
	boolean exists(String fieldName, String value);

	/**
	 * 分页查询
	 * 
	 * @param bs
	 * @return
	 */
	Page<T> searchPage(BaseSearcher bs);

	/**
	 * 分布查询，但不获取总数，作用在导出时，或者在DB数据比较多时，提高查询响应时间
	 * 
	 * @param bs
	 * @return
	 */
	Page<T> queryPage(BaseSearcher bs);

	/**
	 * 转换成Enum方便在页面上显示
	 * 
	 * @return
	 */
	List<EnumObject> findToEnumList();

	/**
	 * 转换成Enum方便在页面上显示
	 * 
	 * @return
	 */
	Map<String, EnumObject> findToEnumMap();

	/**
	 * 转换EnumObject对像值
	 * 
	 * @param m
	 * @param eo
	 */
	void convertEnumObject(T m, EnumObject eo);

}
