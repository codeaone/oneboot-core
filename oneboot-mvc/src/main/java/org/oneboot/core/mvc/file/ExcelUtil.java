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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sargeraswang.util.ExcelUtil.ExcelCell;
import com.sargeraswang.util.ExcelUtil.ExcelLog;
import com.sargeraswang.util.ExcelUtil.ExcelLogs;
import com.sargeraswang.util.ExcelUtil.FieldForSortting;

/**
 * 
 * @author tushiqiao
 * @version $Id: ExcelUtil.java, v 0.1 2017???11???6??? ??????7:46:45 tushiqiao Exp $
 */
public class ExcelUtil {

    private ExcelUtil() {

    }

    private static Logger LG = LoggerFactory.getLogger(ExcelUtil.class);

    @SuppressWarnings("rawtypes")
    public static List<Map> readExcel(String path) throws FileNotFoundException {
        File f = new File(path);
        InputStream inputStream = new FileInputStream(f);

        ExcelLogs logs = new ExcelLogs();
        List<Map> importExcel = importExcel(Map.class, inputStream, "yyyy/MM/dd HH:mm:ss", logs, 0);
        return importExcel;
    }

    /**
     * ???Excel??????????????????voList
     *
     * @param clazz
     *            vo???Class
     * @param inputStream
     *            excel?????????
     * @param pattern
     *            ??????????????????????????????????????????????????????"yyy-MM-dd"
     * @param logs
     *            ??????log??????
     * @param arrayCount
     *            ??????vo??????????????????,????????????index??????,?????????????????????????????????.
     * @return voList
     * @throws RuntimeException
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> importExcel(Class<T> clazz, InputStream inputStream, String pattern, ExcelLogs logs,
            Integer... arrayCount) {
        Workbook workBook;
        try {
            workBook = WorkbookFactory.create(inputStream);
        } catch (Exception e) {
            LG.error("load excel file error", e);
            return null;
        }
        List<T> list = new ArrayList<>();
        Sheet sheet = workBook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        try {
            List<ExcelLog> logList = new ArrayList<>();
            // Map<title,index>
            Map<String, Integer> titleMap = new LinkedHashMap<>();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0) {
                    if (clazz == Map.class) {
                        // ??????map??????key,??????excel?????????
                        Iterator<Cell> cellIterator = row.cellIterator();
                        Integer index = 0;
                        while (cellIterator.hasNext()) {
                            String value = cellIterator.next().getStringCellValue();
                            titleMap.put(value, index);
                            index++;
                        }
                    }
                    continue;
                }
                // ????????????????????????
                boolean allRowIsNull = true;
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Object cellValue = getCellValue(cellIterator.next());
                    if (cellValue != null) {
                        allRowIsNull = false;
                        break;
                    }
                }
                if (allRowIsNull) {
                    LG.warn("Excel row " + row.getRowNum() + " all row value is null!");
                    continue;
                }
                StringBuilder log = new StringBuilder();
                if (clazz == Map.class) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    for (String k : titleMap.keySet()) {
                        Integer index = titleMap.get(k);
                        Cell cell = row.getCell(index);
                        // ??????
                        if (cell == null) {
                            map.put(k, null);
                        } else {
                            cell.setCellType(CellType.STRING);
                            String value = cell.getStringCellValue();
                            if (StringUtils.isNotBlank(value)) {
                                value = StringUtils.trim(value);
                            }
                            map.put(k, value);
                        }
                    }
                    list.add((T) map);

                } else {
                    T t = clazz.newInstance();
                    int arrayIndex = 0;// ??????????????????????????????
                    int cellIndex = 0;// ???????????????????????????????????????cell???
                    List<FieldForSortting> fields = sortFieldByAnno(clazz);
                    for (FieldForSortting ffs : fields) {
                        Field field = ffs.getField();
                        field.setAccessible(true);
                        if (field.getType().isArray()) {
                            Integer count = arrayCount[arrayIndex];
                            Object[] value;
                            if (field.getType().equals(String[].class)) {
                                value = new String[count];
                            } else {
                                // ???????????????String[]???Double[]
                                value = new Double[count];
                            }
                            for (int i = 0; i < count; i++) {
                                Cell cell = row.getCell(cellIndex);
                                String errMsg = validateCell(cell, field, cellIndex);
                                if (StringUtils.isBlank(errMsg)) {
                                    value[i] = getCellValue(cell);
                                } else {
                                    log.append(errMsg);
                                    log.append(";");
                                    logs.setHasError(true);
                                }
                                cellIndex++;
                            }
                            field.set(t, value);
                            arrayIndex++;
                        } else {
                            Cell cell = row.getCell(cellIndex);
                            String errMsg = validateCell(cell, field, cellIndex);
                            if (StringUtils.isBlank(errMsg)) {
                                Object value = null;
                                // ??????????????????,Excel??????String,?????????Bean???Date
                                if (field.getType().equals(Date.class) && cell.getCellTypeEnum() == CellType.STRING) {
                                    Object strDate = getCellValue(cell);
                                    try {
                                        value = new SimpleDateFormat(pattern).parse(strDate.toString());
                                    } catch (ParseException e) {

                                        errMsg = MessageFormat.format("the cell [{}] can not be converted to a date ",
                                                CellReference.convertNumToColString(cell.getColumnIndex()));
                                    }
                                } else {
                                    value = getCellValue(cell);
                                    // ??????????????????,excel???value???String,???bean????????????,???defaultValue?????????,??????=defaultValue
                                    ExcelCell annoCell = field.getAnnotation(ExcelCell.class);
                                    if (value instanceof String && !field.getType().equals(String.class)
                                            && StringUtils.isNotBlank(annoCell.defaultValue())) {
                                        value = annoCell.defaultValue();
                                    }
                                }
                                field.set(t, value);
                            }
                            if (StringUtils.isNotBlank(errMsg)) {
                                log.append(errMsg);
                                log.append(";");
                                logs.setHasError(true);
                            }
                            cellIndex++;
                        }
                    }
                    list.add(t);
                    logList.add(new ExcelLog(t, log.toString(), row.getRowNum() + 1));
                }
            }
            logs.setLogList(logList);
        } catch (InstantiationException e) {
            throw new RuntimeException(MessageFormat.format("can not instance class:{}", clazz.getSimpleName()), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(MessageFormat.format("can not instance class:{}", clazz.getSimpleName()), e);
        }
        return list;
    }

    /**
     * ????????????excel???Vo???????????????????????? <br>
     * Map<????????????,???????????????Cell??????>
     */
    private static Map<Class<?>, CellType[]> validateMap = new LinkedHashMap<>();

    static {
        validateMap.put(String[].class, new CellType[] { CellType.STRING });
        validateMap.put(Double[].class, new CellType[] { CellType.NUMERIC });
        validateMap.put(String.class, new CellType[] { CellType.STRING });
        validateMap.put(Double.class, new CellType[] { CellType.NUMERIC });
        validateMap.put(Date.class, new CellType[] { CellType.NUMERIC, CellType.STRING });
        validateMap.put(Integer.class, new CellType[] { CellType.NUMERIC });
        validateMap.put(Float.class, new CellType[] { CellType.NUMERIC });
        validateMap.put(Long.class, new CellType[] { CellType.NUMERIC });
        validateMap.put(Boolean.class, new CellType[] { CellType.BOOLEAN });
    }

    /**
     * ??????cell?????????????????????
     *
     * @param cellType
     * 
     *            <pre>
     *                 CellType.BLANK
     *                 CellType.BOOLEAN
     *                 CellType.ERROR
     *                 CellType.FORMULA
     *                 CellType.NUMERIC
     *                 CellType.STRING
     *            </pre>
     * 
     * @return
     */
    private static String getCellTypeByInt(CellType cellType) {
        if (cellType == CellType.BLANK) {
            return "Null type";
        } else if (cellType == CellType.BOOLEAN) {
            return "Boolean type";
        } else if (cellType == CellType.ERROR) {
            return "Error type";
        } else if (cellType == CellType.FORMULA) {
            return "Formula type";
        } else if (cellType == CellType.NUMERIC) {
            return "Numeric type";
        } else if (cellType == CellType.STRING) {
            return "String type";
        } else {
            return "Unknown type";
        }
    }

    /**
     * ??????????????????
     *
     * @param cell
     * @return
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null
                || (cell.getCellTypeEnum() == CellType.STRING && StringUtils.isBlank(cell.getStringCellValue()))) {
            return null;
        }
        CellType cellType = cell.getCellTypeEnum();
        if (cellType == CellType.BLANK) {
            return null;
        } else if (cellType == CellType.BOOLEAN) {
            return cell.getBooleanCellValue();
        } else if (cellType == CellType.ERROR) {
            return cell.getErrorCellValue();
        } else if (cellType == CellType.FORMULA) {
            try {
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            } catch (IllegalStateException e) {
                return cell.getRichStringCellValue();
            }
        } else if (cellType == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            } else {
                return cell.getNumericCellValue();
            }
        } else if (cellType == CellType.STRING) {
            return cell.getStringCellValue();
        } else {
            return null;
        }
    }

    /**
     * ??????Cell??????????????????
     *
     * @param cell
     *            cell?????????
     * @param field
     *            ??????
     * @param cellNum
     *            ???????????????,??????errMsg
     * @return
     */
    private static String validateCell(Cell cell, Field field, int cellNum) {
        String columnName = CellReference.convertNumToColString(cellNum);
        String result = null;
        CellType[] cellTypeArr = validateMap.get(field.getType());
        if (cellTypeArr == null) {
            result = MessageFormat.format("Unsupported type [{}]", field.getType().getSimpleName());
            return result;
        }
        ExcelCell annoCell = field.getAnnotation(ExcelCell.class);
        if (cell == null
                || (cell.getCellTypeEnum() == CellType.STRING && StringUtils.isBlank(cell.getStringCellValue()))) {
            if (annoCell != null && annoCell.valid().allowNull() == false) {
                result = MessageFormat.format("the cell [{}] can not null", columnName);
            }
            ;
        } else if (cell.getCellTypeEnum() == CellType.BLANK && annoCell.valid().allowNull()) {
            return result;
        } else {
            List<CellType> cellTypes = Arrays.asList(cellTypeArr);

            // ?????????????????????????????????,?????????????????????
            if (!(cellTypes.contains(cell.getCellTypeEnum()))
                    || StringUtils.isNotBlank(annoCell.defaultValue()) && cell.getCellTypeEnum() == CellType.STRING) {
                StringBuilder strType = new StringBuilder();
                for (int i = 0; i < cellTypes.size(); i++) {
                    CellType cellType = cellTypes.get(i);
                    strType.append(getCellTypeByInt(cellType));
                    if (i != cellTypes.size() - 1) {
                        strType.append(",");
                    }
                }
                result = MessageFormat.format("the cell [{}] type must [{}]", columnName, strType.toString());
            } else {
                // ??????????????????,??????????????????????????????
                // String in
                if (annoCell.valid().in().length != 0 && cell.getCellTypeEnum() == CellType.STRING) {
                    String[] in = annoCell.valid().in();
                    String cellValue = cell.getStringCellValue();
                    boolean isIn = false;
                    for (String str : in) {
                        if (str.equals(cellValue)) {
                            isIn = true;
                        }
                    }
                    if (!isIn) {
                        result = MessageFormat.format("the cell [{}] value must in {}", columnName, in);
                    }
                }
                // ?????????
                if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                    double cellValue = cell.getNumericCellValue();
                    // ??????
                    if (!Double.isNaN(annoCell.valid().lt())) {
                        if (!(cellValue < annoCell.valid().lt())) {
                            result = MessageFormat.format("the cell [{}] value must less than [{}]", columnName,
                                    annoCell.valid().lt());
                        }
                    }
                    // ??????
                    if (!Double.isNaN(annoCell.valid().gt())) {
                        if (!(cellValue > annoCell.valid().gt())) {
                            result = MessageFormat.format("the cell [{}] value must greater than [{}]", columnName,
                                    annoCell.valid().gt());
                        }
                    }
                    // ????????????
                    if (!Double.isNaN(annoCell.valid().le())) {
                        if (!(cellValue <= annoCell.valid().le())) {
                            result = MessageFormat.format("the cell [{}] value must less than or equal [{}]",
                                    columnName, annoCell.valid().le());
                        }
                    }
                    // ????????????
                    if (!Double.isNaN(annoCell.valid().ge())) {
                        if (!(cellValue >= annoCell.valid().ge())) {
                            result = MessageFormat.format("the cell [{}] value must greater than or equal [{}]",
                                    columnName, annoCell.valid().ge());
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * ??????annotation???seq??????????????????
     *
     * @param clazz
     * @return
     */
    private static List<FieldForSortting> sortFieldByAnno(Class<?> clazz) {
        Field[] fieldsArr = clazz.getDeclaredFields();
        List<FieldForSortting> fields = new ArrayList<>();
        List<FieldForSortting> annoNullFields = new ArrayList<>();
        for (Field field : fieldsArr) {
            ExcelCell ec = field.getAnnotation(ExcelCell.class);
            if (ec == null) {
                // ??????ExcelCell Annotation ???????????????
                continue;
            }
            int id = ec.index();
            fields.add(new FieldForSortting(field, id));
        }
        fields.addAll(annoNullFields);
        sortByProperties(fields, true, false, "index");
        return fields;
    }

    @SuppressWarnings({ "unchecked" })
    private static void sortByProperties(List<? extends Object> list, boolean isNullHigh, boolean isReversed,
            String... props) {
        if (CollectionUtils.isNotEmpty(list)) {
            Comparator<?> typeComp = ComparableComparator.getInstance();
            if (isNullHigh == true) {
                typeComp = ComparatorUtils.nullHighComparator(typeComp);
            } else {
                typeComp = ComparatorUtils.nullLowComparator(typeComp);
            }
            if (isReversed) {
                typeComp = ComparatorUtils.reversedComparator(typeComp);
            }

            List<Object> sortCols = new ArrayList<Object>();

            if (props != null) {
                for (String prop : props) {
                    sortCols.add(new BeanComparator(prop, typeComp));
                }
            }
            if (sortCols.size() > 0) {
                Comparator<Object> sortChain = new ComparatorChain(sortCols);
                Collections.sort(list, sortChain);
            }
        }
    }
}
