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
package org.oneboot.core.cache.manager;

public enum CacheName implements ICacheName {
    DICTIONARY("数据字典"), DYNAMIC_GROOVY("动态脚本"),;

    private final String code;
    private final String name;
    private final String desc;

    /**
     * @param code
     * @param name
     * @param desc
     */
    private CacheName(String code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    private CacheName(String name) {
        this.name = name;
        this.code = this.name();
        this.desc = this.name();
    }

    /**
     * 根据枚举代码获取枚举信息
     * 
     * @param code
     * @return
     */
    public static CacheName get(String code) {
        for (CacheName cacheName : CacheName.values()) {
            if (cacheName.getCode().equalsIgnoreCase(code)) {
                return cacheName;
            }
        }

        return null;
    }

    /**
     * 获取枚举代码
     * 
     * @return
     */
    public String getCode() {
        return this.code;
    }

    /**
     * @see com.huanwu.eboot.cache.manager.CacheName#getName()
     */
    public String getName() {
        return this.name;
    }

    /**
     * @see com.huanwu.eboot.cache.manager.CacheName#getDesc()
     */
    public String getDesc() {
        return this.desc;
    }

    /**
     * 
     * @return
     */
    public Object getDescription() {
        return null;
    }

}
