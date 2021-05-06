package org.oneboot.core.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BeginEndTimeQuery {
	/**
	 * 开始时间
	 */
	private LocalDate startTime;

	/**
	 * 结束时间
	 */
	private LocalDate endTime;

	public void setDate(LocalDate date) {
		startTime = date;
		endTime = date.plusDays(1);
	}

	public static BeginEndTimeQuery setCurDate(LocalDate date) {
		BeginEndTimeQuery query = new BeginEndTimeQuery();
		query.setStartTime(date);
		query.setEndTime(date.plusDays(1));
		return query;
	}

}
