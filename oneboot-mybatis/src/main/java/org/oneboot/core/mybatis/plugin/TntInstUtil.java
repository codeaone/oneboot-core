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
package org.oneboot.core.mybatis.plugin;

import org.apache.commons.lang3.StringUtils;


/**
 * 租户信息操作工具类
 * 
 * @author tushiqiao
 * @version $Id: TntInstUtil.java, v 0.1 2016年3月20日 下午8:47:16 tuzhe420 Exp $
 */
public class TntInstUtil {

    /**
     * 获取租户
     */
    public static String getTntInstId() {
        try {
            /*
             * SofaTraceContext sofaTraceContext = SofaTraceContextHolder.getSofaTraceContext(); SofaTracerSpan
             * sofaTracerSpan = sofaTraceContext.getCurrentSpan(); if(sofaTracerSpan == null) { return "SMARTDEF"; }
             * SofaTracerSpanContext sofaTracerSpanContext = sofaTracerSpan.getSofaTracerSpanContext();
             */
            // String traceId = sofaTracerSpanContext.getTraceId();
            // String spanId = sofaTracerSpanContext.getSpanId();

            // 获取 bizBaggage
            // Map<String, String> bizBaggages = sofaTracerSpanContext.getBizBaggage();
            // 获取 sysBaggage
            // Map<String, String> sysBaggages = sofaTracerSpanContext.getSysBaggage();

            String tntInstId = null;
            /*
             * if(sysBaggages !=null) { tntInstId = sysBaggages.get("tntInstId"); }
             */

            // 从上下文中获取
            /*
             * try { EventContext context = EventContextUtil.getEventContextFromTracer(); tntInstId =
             * context.getTntInstId(); } catch (Exception e) { //LoggerUtil.error(e, "租户信息从上下文中获取失败，设置默认值：${}",
             * tntInstId); }
             */

            // 获取到空的租户信息或获取失败，返回默认
            if (StringUtils.isBlank(tntInstId)) {
                tntInstId = "SMARTDEF";
                // LoggerUtil.error("租户信息从上下文中获取为空，设置默认值：${}", tntInstId);
            }

            // 返回
            return tntInstId;
        } catch (Throwable e) {
            return "SMARTDEF";
        }

    }

}
