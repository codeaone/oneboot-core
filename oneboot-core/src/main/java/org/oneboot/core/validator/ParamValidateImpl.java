package org.oneboot.core.validator;

import org.oneboot.core.exception.CommonErrorCode;
import org.oneboot.core.exception.ObootException;

public class ParamValidateImpl implements ParamValidate {

	@Override
	public void validate(Object param) {
		 // 非空校验
        if (null == param) {
            throw new ObootException(CommonErrorCode.ILLEGAL_PARAMETERS);// CustomerException
        }

        ValidationResult result = ValidateUtils.validateEntity(param);
        if (result.isHasErrors()) {
            throw new ObootException(CommonErrorCode.ILLEGAL_PARAMETERS, result.getErrorMsg().toString());
        }
	}

}
