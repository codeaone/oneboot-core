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

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.oneboot.core.mybatis.model.BaseSearcher;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 
 * @author tushiqiao
 * @version $Id: BaseDAO.java, v 0.1 2019年8月11日 下午11:36:10 tushiqiao Exp $
 */
public interface BaseDAO<D> extends BaseMapper<D> {

    /**
     * 修改
     *
     * @param entity
     * @return int
     */
    int update(D entity);

    /**
     * 批量更新字段
     * 
     * @param idList
     * @param field
     * @param value
     * @return
     */
    int updateBatchField(@Param("idList") List<String> idList, @Param("field") String field,
            @Param("value") Object value);

    /**
     * 物理删除
     * 
     * @param id
     * @return
     */
    int delete(@Param("id") String id);

    /**
     * 根据 id 查询 entity
     * 
     * @param id
     * @return
     */
    D findById(@Param("id") String id);

    /**
     * 查询所有数据
     * 
     * @param tntInstId
     * @return
     */
    List<D> findAll(@Param("tntInstId") String tntInstId);

    /**
     * 根据id列表查询数据
     * 
     * @param ids
     * @return
     */
    List<D> findAllByIds(@Param("ids") List<String> ids);

    /**
     * 查询数据列表 分页查询
     * 
     * @param innerSearcher
     * @return
     */
    List<D> searchPage(BaseSearcher innerSearcher);
}
