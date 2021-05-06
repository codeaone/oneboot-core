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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionUtils;
import org.oneboot.core.enums.EnumObject;
import org.oneboot.core.lang.Page;
import org.oneboot.core.logging.LoggerUtil;
import org.oneboot.core.mybatis.model.BaseSearcher;
import org.oneboot.core.mybatis.wrapper.QueryWrapperUtils;
import org.oneboot.core.utils.PinyinFormatUtil;
import org.springframework.beans.BeanUtils;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;

import lombok.extern.slf4j.Slf4j;

/**
 * @param <M>
 * @param <D>
 *            泛型：M 是 mapper 对象
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
@Slf4j
public abstract class BaseRepositoryImpl<M extends BaseModel, D> extends AbstractRepositoryImpl
		implements IRepository<M, D> {

	protected BaseMapper<D> baseMapper;

	protected Class<M> entityClass = currentModelClass();

	protected Class<D> mapperClass = currentMapperClass();

	public BaseRepositoryImpl(BaseMapper<D> baseMapper) {
		super();
		this.baseMapper = baseMapper;
	}

	@Override
	public Class<D> getDoClazz() {
		return mapperClass;
	}

	@Override
	public Class<M> getMclazz() {
		return entityClass;
	}

	@Override
	public D create() {
		if (mapperClass != null) {
			try {
				return mapperClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				log.error("mapperClass.newInstance error", e);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected Class<D> currentMapperClass() {
		return (Class<D>) ReflectionKit.getSuperClassGenericType(getClass(), 0);
	}

	@SuppressWarnings("unchecked")
	protected Class<M> currentModelClass() {
		return (Class<M>) ReflectionKit.getSuperClassGenericType(getClass(), 1);
	}

	@Override
	public boolean save(M entity) {
		outExtMap(entity);
		D obj = create();
		BeanUtils.copyProperties(entity, obj);
		return retBool(baseMapper.insert(obj));
	}

	@Override
	public boolean saveBatch(Collection<M> eList, int batchSize) {
		List<D> list = copyOutDoList(eList, getDoClazz());

		String sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE);
		try (SqlSession batchSqlSession = sqlSessionBatch()) {
			int i = 0;
			for (D anEntityList : list) {
				batchSqlSession.insert(sqlStatement, anEntityList);
				if (i >= 1 && i % batchSize == 0) {
					batchSqlSession.flushStatements();
				}
				i++;
			}
			batchSqlSession.flushStatements();
		}
		return true;
	}

	private String getSqlStatement(SqlMethod sqlMethod) {
		return SqlHelper.getSqlStatement(mapperClass, sqlMethod);
	}

	@Override
	public boolean updateById(M entity) {
		outExtMap(entity);
		entity.setGmtModified(new Date());
		D obj = create();
		BeanUtils.copyProperties(entity, obj);
		return retBool(baseMapper.updateById(obj));
	}

	@Override
	public boolean updatePatchById(M entity) {
		entity.setGmtModified(new Date());
		D obj = create();
		BeanUtils.copyProperties(entity, obj);
		return retBool(baseMapper.updateById(obj));
	}

	@Override
	public int updateBatchField(List<String> idList, String field, Object value) {
		D obj = create();
		setProperty(obj, field, value);
		TableInfo info = TableInfoHelper.getTableInfo(getDoClazz());
		UpdateWrapper<D> uw = new UpdateWrapper<>();
		uw.in(info.getKeyColumn(), idList);
		return baseMapper.update(obj, uw);
	}

	@Override
	public boolean removeById(Object id) {
		return retBool(baseMapper.deleteById((Serializable) id));
	}

	@Override
	public boolean removeByMap(Map<String, Object> columnMap) {
		return retBool(baseMapper.deleteByMap(QueryWrapperUtils.getColumnMap(columnMap)));
	}

	@Override
	public boolean removeByMap(String column, Object value) {
		Map<String, Object> columnMap = new HashMap<>(16);
		columnMap.put(column, value);
		return removeByMap(columnMap);
	}

	@Override
	public M findById(Object id) {
		D obj = baseMapper.selectById((Serializable) id);
		return copyProperties(obj, getMclazz());
	}

	@Override
	public M findOne(Wrapper<D> wrapper) {
		D obj = baseMapper.selectOne(wrapper);
		return copyProperties(obj, getMclazz());
	}

	@Override
	public M findOne(Map<String, Object> columnMap) {
		QueryWrapper<D> wrapper = new QueryWrapper<>();
		QueryWrapperUtils.setMapWrapper(wrapper, columnMap);
		D obj = baseMapper.selectOne(wrapper);
		return copyProperties(obj, getMclazz());
	}

	@Override
	public M findOne(String column, Object value) {
		Map<String, Object> map = new HashMap<>(16);
		map.put(column, value);
		return findOne(map);
	}

	@Override
	public List<M> findList(String column, Object value) {
		Map<String, Object> map = new HashMap<>(16);
		map.put(column, value);
		return findList(map);
	}

	@Override
	public List<M> findList(Map<String, Object> columnMap) {
		List<D> list = baseMapper.selectByMap(QueryWrapperUtils.getColumnMap(columnMap));
		return copyPropertiesList(list, getMclazz());
	}

	@Override
	public List<M> findAll() {
		// 在这里要做分页操作的，万一数据量过大怎么办呢
		QueryWrapper<D> wrapper = new QueryWrapper<>();
		// TODO limit 参数不清楚怎么弄呀
		wrapper.last(" limit 20000");
		List<D> list = baseMapper.selectList(wrapper);
		return copyPropertiesList(list, getMclazz());
	}

	@Override
	public List<M> findAll(String... ids) {
		List<D> list = baseMapper.selectBatchIds(Arrays.asList(ids));
		return copyPropertiesList(list, getMclazz());
	}

	@Override
	public List<M> findList(BaseSearcher bs) {
		QueryWrapper<D> wrapper = new QueryWrapper<>();
		QueryWrapperUtils.setQueryWrapper(wrapper, bs);
		List<D> list = baseMapper.selectList(wrapper);
		return copyPropertiesList(list, getMclazz());
	}

	@Override
	public List<M> findListAllPage(BaseSearcher bs) {
		List<D> list = new ArrayList<>();
		bs.setDefaultParam();
		int pageNo = 1;
		int pageSize = 10000;
		com.baomidou.mybatisplus.extension.plugins.pagination.Page<D> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
				pageNo, pageSize);
		QueryWrapper<D> wrapper = new QueryWrapper<>();
		QueryWrapperUtils.setQueryWrapper(wrapper, bs);

		IPage<D> result = baseMapper.selectPage(page, wrapper);

		// 得到总页数据，然后for查询
		// 分页查询数据
		LoggerUtil.info("查询总数：{0}", result.getTotal());

		list.addAll(result.getRecords());
		long totalPageSize = result.getPages();

		for (long i = 2; i <= totalPageSize; i++) {
			page.setCurrent(i);
			page.setSearchCount(false);
			result = baseMapper.selectPage(page, wrapper);
			list.addAll(result.getRecords());
		}
		return copyPropertiesList(list, getMclazz());
	}

	@Override
	public List<M> findList(Wrapper<D> wrapper) {
		List<D> list = baseMapper.selectList(wrapper);
		return copyPropertiesList(list, getMclazz());
	}

	@Override
	public boolean exists(String fieldName, String value) {
		Map<String, Object> columnMap = new HashMap<>(16);
		columnMap.put(QueryWrapperUtils.getLowerCamel(fieldName), value);
		List<D> list = baseMapper.selectByMap(columnMap);
		return list != null && list.size() > 0;
	}

	@Override
	public Page<M> searchPage(BaseSearcher bs) {
		bs.setDefaultParam();
		com.baomidou.mybatisplus.extension.plugins.pagination.Page<D> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
				bs.getPageNo(), bs.getPageSize());
		QueryWrapper<D> wrapper = new QueryWrapper<>();
		QueryWrapperUtils.setQueryWrapper(wrapper, bs);

		IPage<D> result = baseMapper.selectPage(page, wrapper);
		return copyToPage(result, getMclazz());
	}

	@Override
	public Page<M> queryPage(BaseSearcher bs) {
		bs.setDefaultParam();
		com.baomidou.mybatisplus.extension.plugins.pagination.Page<D> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
				bs.getPageNo(), bs.getPageSize());
		QueryWrapper<D> wrapper = new QueryWrapper<>();
		QueryWrapperUtils.setQueryWrapper(wrapper, bs);
		// 不进行count语句
		page.setSearchCount(false);
		IPage<D> result = baseMapper.selectPage(page, wrapper);
		return copyToPage(result, getMclazz());
	}

	@Override
	public List<EnumObject> findToEnumList() {
		List<M> list = findAll();
		return toEnumList(list);
	}

	public List<EnumObject> toEnumList(List<M> list) {
		List<EnumObject> enumList = new ArrayList<>();
		for (M m : list) {
			EnumObject eo = new EnumObject();
			convertEnumObject(m, eo);
			if (StringUtils.isNotBlank(eo.getName())) {
				eo.setPinyin(PinyinFormatUtil.formatPinYinStr(eo.getName()));
			}
			enumList.add(eo);
		}
		return enumList;
	}

	@Override
	public Map<String, EnumObject> findToEnumMap() {
		Map<String, EnumObject> map = new HashMap<String, EnumObject>(16);
		List<EnumObject> enumList = findToEnumList();
		for (EnumObject e : enumList) {
			map.put(e.getValue(), e);
		}
		return map;
	}

	@Override
	public void convertEnumObject(M m, EnumObject eo) {
	}

	/**
	 * 判断数据库操作是否成功
	 *
	 * @param result
	 *            数据库操作返回影响条数
	 * @return boolean
	 */
	protected boolean retBool(Integer result) {
		return SqlHelper.retBool(result);
	}

	/**
	 * 批量操作 SqlSession
	 */
	protected SqlSession sqlSessionBatch() {
		return SqlHelper.sqlSessionBatch(currentModelClass());
	}

	/**
	 * 释放sqlSession
	 *
	 * @param sqlSession
	 *            session
	 */
	protected void closeSqlSession(SqlSession sqlSession) {
		SqlSessionUtils.closeSqlSession(sqlSession, GlobalConfigUtils.currentSessionFactory(currentModelClass()));
	}

}
