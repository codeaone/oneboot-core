package org.oneboot.core.template;

import org.oneboot.core.context.SceneCode;
import org.oneboot.core.result.CommonGwResult;
import org.oneboot.core.result.CommonRpcResult;
import org.slf4j.Logger;

public interface CommonOperateTemplate {
	 /**
     * 模板方法
     * 
     * @param logger
     *            日志
     * @param bizName
     *            业务名称
     * @param sceneCode
     *            场景信息
     * @param result
     *            返回结果
     * @param callBack
     *            回调
     */
    public <T extends CommonRpcResult> T operate(Logger logger, String bizName, SceneCode sceneCode, Class<T> clazz,
            CommonOperateCallback<T> callback);

    /**
     * 针对网关的模板方法
     * 
     * @param logger
     *            日志
     * @param bizName
     *            业务名称
     * @param sceneCode
     *            场景信息
     * @param result
     *            返回结果
     * @param callBack
     *            回调
     */
    public <T extends CommonGwResult> T operateGw(Logger logger, String bizName, SceneCode sceneCode, Class<T> clazz,
            GatewayOperateCallback<T> callback);
}
