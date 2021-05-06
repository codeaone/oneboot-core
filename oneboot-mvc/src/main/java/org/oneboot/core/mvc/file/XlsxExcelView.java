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
package org.oneboot.core.mvc.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.oneboot.core.exception.CommonErrorCode;
import org.oneboot.core.exception.ObootException;
import org.oneboot.core.lang.math.Money;
import org.oneboot.core.logging.LoggerUtil;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

/**
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public class XlsxExcelView extends AbstractXlsxView {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/** 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ExcelObject eo = (ExcelObject) model.get("ExcelObject");

		long t = System.currentTimeMillis();
		LoggerUtil.info("start export excel file {0}, size:{1}", eo.getFilename(), eo.getDatas().size());
		String filename = new String(eo.getFilename().getBytes("gbk"), "iso-8859-1");

		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xlsx\"");
		// 告诉浏览器用什么软件可以打开此文件
		response.setHeader("content-Type", "application/vnd.ms-excel");

		// create excel xls sheet
		Sheet sheet = workbook.createSheet("内容");
		try {
			Map<String, String> titles = eo.getTitles();

			// create header row
			Row header = sheet.createRow(0); // 这里需要一个list来传入
			int cellIndex = 0;
			for (Map.Entry<String, String> entry : titles.entrySet()) {
				header.createCell(cellIndex++).setCellValue(entry.getValue());
			}

			// Create data cells
			int rowCount = 1;
			for (Object data : eo.getDatas()) {
				Row dataRow = sheet.createRow(rowCount++);
				setDataCellValue(dataRow, titles, data);
			}
		} catch (Exception e) {
			LoggerUtil.error(e, "文件导出异常 {0}", filename);
		}
		LoggerUtil.info("export excel file {0} 耗时：{1}ms", eo.getFilename(), System.currentTimeMillis() - t);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setSheetData(Workbook workbook, String sheetName, ExcelObject eo) {
		// create excel xls sheet
		Sheet sheet = workbook.createSheet(sheetName);

		Map<String, String> titles = eo.getTitles();

		// create header row
		Row header = sheet.createRow(0); // 这里需要一个list来传入
		int cellIndex = 0;
		for (Map.Entry<String, String> entry : titles.entrySet()) {
			header.createCell(cellIndex++).setCellValue(entry.getValue());
		}

		// Create data cells
		int rowCount = 1;
		for (Object data : eo.getDatas()) {
			Row dataRow = sheet.createRow(rowCount++);
			setDataCellValue(dataRow, titles, data);
		}
	}

	/**
	 * 保存表格到本地磁盘 SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
	 * setSheetData(workbook, "内容", eo);
	 * 
	 * @param workbook
	 * @param path
	 * @param fileName
	 * @return
	 */
	public String saveWorkbookToDisk(SXSSFWorkbook workbook, String path, String fileName) {
		String filePath = path + fileName + StringUtils.substring(UUID.randomUUID().toString(), -4) + ".xlsx";
		// 解决中文名乱码问题
		System.setProperty("sun.jnu.encoding", "utf-8");
		File file = new File(filePath);
		File fileParent = file.getParentFile();
		if (!fileParent.exists()) {
			fileParent.mkdirs();
			LoggerUtil.info("文件夹创建成功{0}", fileParent.getPath());
		}
		try {
			file.createNewFile();
		} catch (IOException e1) {
			LoggerUtil.error(e1, "");
		}

		// 写到磁盘上
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
			workbook.write(fileOutputStream);
			fileOutputStream.close();
		} catch (Exception e) {
			LoggerUtil.error(e, "写入文件失败{0}", filePath);
		}

		return filePath;
	}

	@SuppressWarnings("rawtypes")
	public String saveExcelToDisk(ExcelObject eo, String path, String fileName) {
		long t = System.currentTimeMillis();
		LoggerUtil.info("start saveExcelToDisk {0},{1},{2}", eo.getDatas().size(), path, fileName);
		SXSSFWorkbook workbook = new SXSSFWorkbook(1000);// 内存中保留 1000
		setSheetData(workbook, "内容", eo);
		String file = saveWorkbookToDisk(workbook, path, fileName);
		LoggerUtil.info("生成文件完成：{0}, 耗时：{1}ms", file, System.currentTimeMillis() - t);
		return file;
	}

	/**
	 * 设置列表内容
	 * 
	 * @param dataRow
	 * @param titles
	 * @param data
	 */
	private void setDataCellValue(Row dataRow, Map<String, String> titles, Object data) {
		int cellIndex = 0;
		for (Map.Entry<String, String> entry : titles.entrySet()) {
			// 通过反射获取对象内容
			String titleKey = entry.getKey();

			try {
				Method readMethod = data.getClass().getMethod("get" + StringUtils.capitalize(titleKey));
				Object value = readMethod.invoke(data);
				if (value != null) {
					if (value instanceof Date) {
						Date date = (Date) value;
						value = dateFormat.format(date);
						dataRow.createCell(cellIndex++).setCellValue(value.toString());
					} else if (value instanceof Integer) {
						Integer m = (Integer) value;
						dataRow.createCell(cellIndex++).setCellValue(m);
					} else if (value instanceof Long) {
						Long m = (Long) value;
						dataRow.createCell(cellIndex++).setCellValue(m);
					} else if (value instanceof Double) {
						Double m = (Double) value;
						dataRow.createCell(cellIndex++).setCellValue(m);
					} else if (value instanceof Float) {
						Float m = (Float) value;
						dataRow.createCell(cellIndex++).setCellValue(m);
					} else if (value instanceof Money) {
						Money m = (Money) value;
						dataRow.createCell(cellIndex++).setCellValue(Double.parseDouble(m.getAmount().toString()));
						// dataRow.createCell(cellIndex++).setCellValue(Double.parseDouble(m.getAmountYuan()));
					} else {
						dataRow.createCell(cellIndex++).setCellValue(value.toString());
					}
				} else {
					dataRow.createCell(cellIndex++).setCellValue("");
				}

			} catch (Exception e) {
				throw new ObootException(CommonErrorCode.SYSTEM_ERROR, e);
			}

		}
	}

	/**
	 * Setter method for property <tt>dateFormat</tt>.
	 * 
	 * @param dateFormat
	 *            value to be assigned to property dateFormat
	 */
	public void setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

}
