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

import java.util.Date;

import org.oneboot.core.logging.ToString;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author shiqiao.pro
 * 
 */
@Setter
@Getter
public class BaseModel extends ToString {

    /** serialVersionUID */
    private static final long serialVersionUID = 3153754033711119538L;

    /** 租户ID */
    private String tntInstId;

    /** 创建时间 */
    private Date gmtCreate;

    /** 修改时间 */
    private Date gmtModified;

    /** 创建人 */
    private String creator;

    /** 更新人 */
    private String updater;

    /** 状态 */
    private String status;
    // 导入时需要用到的字段
    /** 排序 */
    private Integer sort = 0;
    /** 备注 */
    private String memo;
}
