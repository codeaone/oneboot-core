package org.oneboot.core.template;

import org.oneboot.core.context.SceneCode;
import org.oneboot.core.context.ServiceContextHolder;
import org.oneboot.core.error.ErrorLevel;
import org.oneboot.core.exception.CommonErrorCode;
import org.oneboot.core.exception.ErrorCodeEvent;
import org.oneboot.core.exception.IErrorCode;
import org.oneboot.core.exception.ObootException;
import org.oneboot.core.exception.ServiceRpcException;
import org.oneboot.core.lang.BeanUtils;
import org.oneboot.core.lang.StringUtils;
import org.oneboot.core.logging.LoggerUtil;
import org.oneboot.core.result.CommonGwResult;
import org.oneboot.core.result.CommonRpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class CommonOperateTemplateImpl implements CommonOperateTemplate {

    /** 网关的业务异常日志，不记录在common-error里面 */
    private static final Logger GW_BIZ_WARN = LoggerFactory.getLogger("GW-BIZ-WARN");

    /** 事务模板 */
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    protected ApplicationContext applicationContext;

    @SuppressWarnings("deprecation")
    @Override
    public <T extends CommonRpcResult> T operate(Logger logger, String bizName, SceneCode sceneCode, Class<T> clazz,
            CommonOperateCallback<T> callback) {
        // Profiler.enter("执行模板业务");

        final T result = BeanUtils.instantiate(clazz);

        long start = System.currentTimeMillis();
        ServiceContextHolder.setLogger(logger);
        // ServiceContextHolder.setSceneCode(sceneCode);
//        LogHolder.setLogger(logger);

        String successStr = null;

        String log = "执行业务" + bizName + ",处理结果：";
        try {
            // 1. 操作前处理
            try {
                // Profiler.enter("执行模板业务-before");
                callback.before();
            } finally {
                // Profiler.release();
            }

            // 2. 操作执行
            try {
                // callback.execute(result);
                // Profiler.enter("执行模板业务-execute");
                Throwable t = transactionTemplate.execute(new TransactionCallback<Throwable>() {
                    @Override
                    public Throwable doInTransaction(TransactionStatus status) {
                        try {
                            callback.execute(result);
                            return null;
                        } catch (Throwable t) {
                            callback.processException(t, status);
                            return t;
                        }
                    }
                });
                if (t != null) {
                    throw t;
                }
            } finally {
                // Profiler.release();
            }

            // 1. 操作后处理
            try {
                // Profiler.enter("执行模板业务-after");
                callback.after(result);
            } catch (Exception e) {
                LoggerUtil.warn(e, "事务后处理失败，result={}", log, result);
            } finally {
                // Profiler.release();
            }

            // 4. 组装结果
            ResultUtil.buildSuccessCommonResult(result, sceneCode);
            LoggerUtil.info("{}成功！详细信息：result={}", log, result);
            successStr = "成功";
        } /*
           * catch (IdempotentException e) { LoggerUtil.warn(e, "{}幂等控制，result={}", log, result);
           * ResultUtil.buildSuccessCommonResult(result, sceneCode); }
           */catch (ServiceRpcException e) {

            ErrorCodeEvent event = new ErrorCodeEvent();
            event.setError(e);
            IErrorCode errorCode = e.getErrorCode();
            event.setErrorCode(errorCode);
            // 这里需要看是系统异常还是业务异常
            String errorLevel = errorCode.getErrorLevel();
            if (StringUtils.equals(ErrorLevel.ERROR, errorLevel)) {
                LoggerUtil.error(e, "{}服务调用失败，result={}", log, result);
            } else {
                // 不输出堆栈信息
                LoggerUtil.warn("{}服务调用失败，result={}", log, result);
                LoggerUtil.warn(GW_BIZ_WARN, "{}服务调用失败，result={}", log, result);
            }
            applicationContext.publishEvent(event);

            // ResultUtil.buildEventRetryResult(result, sceneCode);
        } catch (ObootException e) {
            ResultUtil.buildFailCommonResult(result, sceneCode, e);

            ErrorCodeEvent event = new ErrorCodeEvent();
            event.setError(e);
            IErrorCode errorCode = e.getErrorCode();
            event.setErrorCode(errorCode);
            // 这里需要看是系统异常还是业务异常
            String errorLevel = errorCode.getErrorLevel();
            if (StringUtils.equals(ErrorLevel.ERROR, errorLevel)) {
                LoggerUtil.error(e, "{}失败，result={}", log, result);
            } else {
                // 不输出堆栈信息
                LoggerUtil.warn("{}失败，result={}, error={}", log, result, e.getMessage());
                LoggerUtil.warn(GW_BIZ_WARN, "{}失败biz，result={}, error={}", log, result, e.getMessage());
            }
            applicationContext.publishEvent(event);

        } catch (Throwable e) {
            ErrorCodeEvent event = new ErrorCodeEvent();
            event.setError(e);
            event.setErrorCode(CommonErrorCode.SYSTEM_ERROR);
            applicationContext.publishEvent(event);
            LoggerUtil.error(e, "{}异常，result={}", log, result);
            // ResultUtil.buildEventRetryResult(result, sceneCode);
        } finally {

            if (StringUtils.isBlank(successStr)) {
                successStr = "失败";
            }
            long time = System.currentTimeMillis() - start;
            LoggerUtil.info("执行【{}】" + successStr + ",耗时:{}ms.", bizName, time);

            ServiceContextHolder.clean();
            // Profiler.release();
//            LogHolder.clean();

        }

        return result;
    }

    @SuppressWarnings("deprecation")
    @Override
    public <T extends CommonGwResult> T operateGw(Logger logger, String bizName, SceneCode sceneCode, Class<T> clazz,
            GatewayOperateCallback<T> callback) {
        final T result = BeanUtils.instantiate(clazz);

        long start = System.currentTimeMillis();
        ServiceContextHolder.setLogger(logger);
        // ServiceContextHolder.setSceneCode(sceneCode);
//        LogHolder.setLogger(logger);

        String successStr = null;

        String log = "执行业务" + bizName + ",处理结果：";
        try {
            // 1. 操作前处理
            try {
                // Profiler.enter("执行模板业务-before");
                callback.before();
            } finally {
                // Profiler.release();
            }

            // 2. 操作执行
            try {
                // callback.execute(result);
                // Profiler.enter("执行模板业务-execute");
                Throwable t = transactionTemplate.execute(new TransactionCallback<Throwable>() {
                    @Override
                    public Throwable doInTransaction(TransactionStatus status) {
                        try {
                            callback.execute(result);
                            return null;
                        } catch (Throwable t) {
                            callback.processException(t, status);
                            return t;
                        }
                    }
                });
                if (t != null) {
                    throw t;
                }
            } finally {
                // Profiler.release();
            }

            // 1. 操作后处理
            try {
                // Profiler.enter("执行模板业务-after");
                callback.after(result);
            } catch (Exception e) {
                LoggerUtil.warn(e, "事务后处理失败，result={}", log, result);
            } finally {
                // Profiler.release();
            }

            // 4. 组装结果
            ResultUtil.buildSuccessCommonGwResult(result, sceneCode);
            LoggerUtil.info("{}成功！详细信息：result={}", log, result);
            successStr = "成功";
        } /*
           * catch (IdempotentException e) { LoggerUtil.warn(e, "{}幂等控制，result={}", log, result);
           * ResultUtil.buildSuccessCommonResult(result, sceneCode); }
           */catch (ServiceRpcException e) {
            ErrorCodeEvent event = new ErrorCodeEvent();
            event.setError(e);
            IErrorCode errorCode = e.getErrorCode();
            event.setErrorCode(errorCode);
            // 这里需要看是系统异常还是业务异常
            String errorLevel = errorCode.getErrorLevel();
            if (StringUtils.equals(ErrorLevel.ERROR, errorLevel)) {
                LoggerUtil.error(e, "{}服务调用失败，result={}", log, result);
            } else {
                // 不输出堆栈信息
                LoggerUtil.error("{}服务调用失败，result={}, error={}", log, result, e.getMessage());
            }
            applicationContext.publishEvent(event);

            // ResultUtil.buildEventRetryResult(result, sceneCode);
        } catch (ObootException e) {
            ErrorCodeEvent event = new ErrorCodeEvent();
            event.setError(e);
            IErrorCode errorCode = e.getErrorCode();
            event.setErrorCode(errorCode);
            // 这里需要看是系统异常还是业务异常
            String errorLevel = errorCode.getErrorLevel();

            applicationContext.publishEvent(event);

            ResultUtil.buildFailCommonGwResult(result, sceneCode, e);

            if (StringUtils.equals(ErrorLevel.ERROR, errorLevel)) {
                LoggerUtil.error(e, "{}失败，result={}", log, result);
            } else {
                // 不输出堆栈信息
                LoggerUtil.warn("{}失败，result={}, error={}", log, result, e.getMessage());
                LoggerUtil.warn(GW_BIZ_WARN, "{}失败biz，result={}, error={}", log, result, e.getMessage());
            }
        } catch (Throwable e) {
            LoggerUtil.error(e, "{}异常，result={}", log, result);
            ErrorCodeEvent event = new ErrorCodeEvent();
            event.setError(e);
            event.setErrorCode(CommonErrorCode.SYSTEM_ERROR);
            applicationContext.publishEvent(event);

            // ResultUtil.buildEventRetryResult(result, sceneCode);
        } finally {

            if (StringUtils.isBlank(successStr)) {
                successStr = "失败";
            }
            long time = System.currentTimeMillis() - start;
            LoggerUtil.info("执行【{}】" + successStr + ",耗时:{}ms.", bizName, time);

            ServiceContextHolder.clean();
            // Profiler.release();
//            LogHolder.clean();

        }

        return result;
    }

}
