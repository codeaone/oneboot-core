package org.oneboot.core.mvc;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.oneboot.core.error.ErrorLevel;
import org.oneboot.core.exception.CommonErrorCode;
import org.oneboot.core.exception.ErrorCodeEvent;
import org.oneboot.core.exception.IErrorCode;
import org.oneboot.core.exception.ObootException;
import org.oneboot.core.lang.Page;
import org.oneboot.core.lang.StringUtils;
import org.oneboot.core.logging.LoggerUtil;
import org.oneboot.core.mybatis.extend.FieldExtMapUtil;
import org.oneboot.core.result.CommonMvcResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

public class ObootBaseController {
	@Autowired
	protected ApplicationContext applicationContext;

	protected Map<String, Object> getSucceedMap() {
		Map<String, Object> map = new HashMap<>(16);
		map.put("success", true);
		map.put("message", "操作成功");
		return map;
	}
	protected Map<String, Object> getFailedMap() {
		Map<String, Object> map = new HashMap<>(16);
		map.put("success", false);
		map.put("message", "操作失败了!");
		return map;
	}

	protected CommonMvcResult<Object> getSucceed() {
		CommonMvcResult<Object> result = new CommonMvcResult<>();
		result.setSuccess(true);
		result.setMessage("操作成功");
		return result;
	}

	protected CommonMvcResult<Object> getFailed() {
		CommonMvcResult<Object> result = new CommonMvcResult<>();
		result.setSuccess(false);
		result.setShowType(0);
		result.setMessage("抱歉，操作失败了！");
		return result;
	}

	/** 基于@ExceptionHandler异常处理 */
	@ExceptionHandler
	@ResponseBody
	public CommonMvcResult<Object> exp(HttpServletRequest request, Exception ex) {

		CommonMvcResult<Object> result = getFailed();

		ErrorCodeEvent event = new ErrorCodeEvent();
		event.setError(ex);
		// 根据不同错误转向不同页面
		if (ex instanceof ObootException) {
			ObootException e = (ObootException) ex;
			buildFailResult(result, e);
			IErrorCode errorCode = e.getErrorCode();
			event.setErrorCode(errorCode);

			// 这里需要看是系统异常还是业务异常
			String errorLevel = errorCode.getErrorLevel();
			if (StringUtils.equals(ErrorLevel.ERROR, errorLevel)) {
				LoggerUtil.error(e, "操作失败:{}", e.getMessage());
			} else {
				// 不输出堆栈信息
				LoggerUtil.error("操作失败:{}", e.getMessage());
			}
		} else if (ex instanceof ConstraintViolationException) {
			ConstraintViolationException cve = (ConstraintViolationException) ex;
			String message = CommonErrorCode.ILLEGAL_PARAMETERS.getDesc();
			result.setMessage(CommonErrorCode.ILLEGAL_PARAMETERS.getView());
			result.setCode(CommonErrorCode.ILLEGAL_PARAMETERS.getCode());
			result.setErrors(cve.getLocalizedMessage());
			event.setErrorCode(CommonErrorCode.ILLEGAL_PARAMETERS);

			LoggerUtil.warn("操作失败:{}", message);
		} else if (ex instanceof BindException) {
			BindException be = (BindException) ex;
			result = resultIllegalFailed(be.getBindingResult());
			event.setErrorCode(CommonErrorCode.ILLEGAL_PARAMETERS);

			// LoggerUtil.warn("操作失败:{}", message);

		} else if (ex instanceof MissingServletRequestParameterException) {
			MissingServletRequestParameterException pe = (MissingServletRequestParameterException) ex;
			String message = "请检查 " + pe.getParameterName() + " 是否正确输入";
			result.setMessage(CommonErrorCode.ILLEGAL_PARAMETERS.getView());
			result.setCode(CommonErrorCode.ILLEGAL_PARAMETERS.getCode());
			result.setErrors(message);
			event.setErrorCode(CommonErrorCode.ILLEGAL_PARAMETERS);

			LoggerUtil.warn("操作失败:{}", message);
		} else if (ex instanceof Throwable) {
			buildFailUnknownResult(result);
			event.setErrorCode(CommonErrorCode.SYSTEM_ERROR);
			LoggerUtil.error(ex, "操作异常Unknown");
		}

		applicationContext.publishEvent(event);

		return result;
	}

	protected void buildFailUnknownResult(CommonMvcResult<Object> result) {
		result.setMessage(CommonErrorCode.UNKNOWN_ERROR.getView());
		result.setCode(CommonErrorCode.UNKNOWN_ERROR.getCode());
		result.setShowType(1);
	}

	protected CommonMvcResult<Object> resultIllegalFailed(BindingResult bindingResult) {
		Map<String, Object> errors = new HashMap<>();
		List<FieldError> allError = bindingResult.getFieldErrors();
		for (FieldError error : allError) {
			errors.put(error.getField(), error.getDefaultMessage());
		}
		LoggerUtil.warn("请求参数错误!error={}", errors);

		CommonMvcResult<Object> result = getFailed();
		result.setMessage(CommonErrorCode.ILLEGAL_PARAMETERS.getView());
		result.setCode(CommonErrorCode.ILLEGAL_PARAMETERS.getCode());
		result.setErrors(errors);

		return result;
	}

	protected Map<String, Object> resultIllegalFailedMap(BindingResult bindingResult) {
		Map<String, Object> errors = new HashMap<>();
		List<FieldError> allError = bindingResult.getFieldErrors();
		for (FieldError error : allError) {
			errors.put(error.getField(), error.getDefaultMessage());
		}
		LoggerUtil.warn("请求参数错误!error={}", errors);

		Map<String, Object> map = new HashMap<>(16);
		map.put("success", false);
		map.put("message", CommonErrorCode.ILLEGAL_PARAMETERS.getView());
		map.put("code", CommonErrorCode.ILLEGAL_PARAMETERS.getCode());
		map.put("showType", 0);
		map.put("errors", errors);
		return map;
	}

	protected void buildFailResult(CommonMvcResult<Object> result, ObootException e) {
		result.setCode(e.getErrorCode().getCode());
		result.setMessage(e.getErrorCode().getView());
		if (e.getShowType() != null) {
			result.setShowType(e.getShowType().intValue());
		}

		if (StringUtils.isNotBlank(e.getErrorDesc())) {
			result.setMessage(e.getErrorDesc());
		}

	}

	protected void warpTheVO(Object source, Object vo) {

	}

	protected <T> List<T> copyPropertiesList(List<? extends Object> source, Class<T> target, String idName) {
		List<T> list = new ArrayList<>();
		if (source != null) {
			for (Object object : source) {
				T obj = BeanUtils.instantiateClass(target);
				readExtMap(object);
				Object voID = getProperty(object, idName);
				BeanUtils.copyProperties(object, obj);
				setIdProperty(obj, "id", voID);
				warpTheVO(object, obj);
				list.add(obj);
			}
		}
		return list;
	}

	protected <T> T copyPropertiesObj(Object source, Class<T> target, String idName) {
		T obj = BeanUtils.instantiateClass(target);
		readExtMap(source);
		Object voID = getProperty(source, idName);
		BeanUtils.copyProperties(source, obj);
		setIdProperty(obj, "id", voID);
		warpTheVO(source, obj);
		return obj;
	}

	protected void outExtMap(Object source) {
		try {
			FieldExtMapUtil.outExtMap(source);
		} catch (Exception e) {
			LoggerUtil.error(e, "outExtMap 出错啦{}", source);
		}
	}

	protected void readExtMap(Object source) {
		try {
			FieldExtMapUtil.readExtMap(source);
		} catch (Exception e) {
			LoggerUtil.error(e, "readExtMap 出错啦{}", source);
		}
	}

	/**
	 * VOpage copy
	 * 
	 * @param source
	 * @param target
	 * @param idName 在页面上需要使用到的ID字段名
	 * @return
	 */
	protected <T> Page<T> copyToPage(Page<? extends Object> source, Class<T> target, String idName) {

		List<T> resultList = copyPropertiesList(source.getResult(), target, idName);
		Page<T> page = new Page<T>();
		page.setPageNo(source.getPageNo());
		page.setPageSize(source.getPageSize());
		page.setTotalCount(source.getTotalCount());
		page.setResult(resultList);
		return page;
	}

	/* 该方法用于传入某实例对象以及对象方法名、修改值，通过放射调用该对象的某个set方法设置修改值 */
	protected void setIdProperty(Object beanObj, String property, Object value) {
		if (value == null) {
			return;
		}
		// 此处应该判断beanObj,property不为null
		try {
			PropertyDescriptor pd = new PropertyDescriptor(property, beanObj.getClass());
			Method setMethod = pd.getWriteMethod();
			if (setMethod == null) {
				return;
			}
			Parameter parm = setMethod.getParameters()[0];
			if (parm.getType().equals(value.getClass())) {
				setMethod.invoke(beanObj, value);
			} else {
				// 类型一致，默认使用String取值
				setMethod.invoke(beanObj, value.toString());
			}
		} catch (Exception e) {
		}
	}

	protected Object getProperty(Object beanObj, String property) {
		// 此处应该判断beanObj,property不为null
		try {
			PropertyDescriptor pd = new PropertyDescriptor(property, beanObj.getClass());
			Method getMethod = pd.getReadMethod();
			if (getMethod == null) {
				return null;
			}
			return getMethod.invoke(beanObj);
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
     * 针对null字段不copy
     * 
     * @param source
     * @param target
     * @param ignoreProperties
     * @throws BeansException
     */
    public void copyProperties(Object source, Object target, String... ignoreProperties) throws BeansException {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null && ClassUtils.isAssignable(writeMethod.getParameterTypes()[0],
                            readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }

                            /*
                             * if (readMethod.getReturnType() == String.class) { if (value != null) {
                             * writeMethod.invoke(target, value); } } else { writeMethod.invoke(target, value); }
                             */
                            if (value != null) {
                                writeMethod.invoke(target, value);
                            }

                        } catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }

}
