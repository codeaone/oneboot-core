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
package org.oneboot.core.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.oneboot.core.lang.math.Money;

/**
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
@MappedTypes({ Money.class })
public class MoneyTypeHandler extends BaseTypeHandler<Money> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Money money, JdbcType jdbcType) throws SQLException {
//		ps.setObject(i, money.getAmountLi());
		ps.setObject(i, money.getAmount());
	}

	@Override
	public Money getNullableResult(ResultSet rs, String columnName) throws SQLException {
		int m = rs.getInt(columnName);
		return new Money(m);
	}

	@Override
	public Money getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		int m = rs.getInt(columnIndex);
		return new Money(m);
	}

	@Override
	public Money getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		int m = cs.getInt(columnIndex);
		return new Money(m);
	}

}
