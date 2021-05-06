package org.oneboot.core.mybatis.model;

import lombok.Data;

@Data
public class DataScopeData {

	/** 字段名 **/
	private String fieldName;
	
	/** 查询值 **/
	private String value;
	
	/** Sql **/
	private String sql;
	
	private boolean isIn = false;
}
