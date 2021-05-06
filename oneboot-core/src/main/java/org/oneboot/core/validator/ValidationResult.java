package org.oneboot.core.validator;

import java.util.Map;

import lombok.Data;

@Data
public class ValidationResult {
	  // 校验结果是否有错
    private boolean hasErrors;

    // 校验错误信息
    private Map<String, String> errorMsg;
}
