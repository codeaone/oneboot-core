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
package org.oneboot.core.mybatis.model;

/**
 * 当结果数据量不大时，我们需要查询出记录总条数
 * 
 * @author shiqiao.pro
 * 
 */
public class BaseTotalSearcher extends BaseSearcher {

    /**  */
    private static final long serialVersionUID = 7879010365257695500L;
    /** 记录总条数 */
    private int totalCount = -1;

    private String tntInstId = "test";

    public BaseTotalSearcher() {
    };

    public BaseTotalSearcher(int pageNo, int pageSize) {
        super(pageNo, pageSize);
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * Getter method for property <tt>tntInstId</tt>.
     * 
     * @return property value of tntInstId
     */
    public String getTntInstId() {
        return tntInstId;
    }

    /**
     * Setter method for property <tt>tntInstId</tt>.
     * 
     * @param tntInstId
     *            value to be assigned to property tntInstId
     */
    public void setTntInstId(String tntInstId) {
        this.tntInstId = tntInstId;
    }

}
