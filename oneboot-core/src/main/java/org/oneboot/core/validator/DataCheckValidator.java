package org.oneboot.core.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.oneboot.core.lang.StringUtils;

public class DataCheckValidator implements ConstraintValidator<DataCheck, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isBlank(value)) {
			// 为空时不做数据检查
			return true;
		}
		return false;
	}

}
