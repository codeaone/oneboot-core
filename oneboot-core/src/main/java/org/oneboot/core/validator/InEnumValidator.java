package org.oneboot.core.validator;

import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.oneboot.core.enums.InEnum;
import org.oneboot.core.lang.StringUtils;
import org.oneboot.core.utils.EnumsUtil;

/**
 * 自定义约束的校验器
 * 
 * @author shiqiao.pro
 * 
 */
public class InEnumValidator implements ConstraintValidator<InEnum, String> {

	/** 枚举值 **/
	private Set<String> values;

	@Override
	public void initialize(InEnum annotation) {
		values = EnumsUtil.toValueList(annotation.value());
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(StringUtils.isBlank(value)) {
			// 空值不做检查
			return true;
		}
		// <2.1> 校验通过
		if (values.contains(value)) {
			return true;
		}
		// <2.2.1>校验不通过，自定义提示语句（因为，注解上的 value 是枚举类，无法获得枚举类的实际值）
		context.disableDefaultConstraintViolation(); // 禁用默认的 message 的值
		context.buildConstraintViolationWithTemplate(
				context.getDefaultConstraintMessageTemplate().replaceAll("\\{value}", values.toString()))
				.addConstraintViolation(); // 重新添加错误提示语句
		return false; 
		// <2.2.2.>
	}

}
