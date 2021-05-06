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
package org.oneboot.core.mvc;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.oneboot.core.context.ServiceContextHolder;
import org.oneboot.core.exception.CommonErrorCode;
import org.oneboot.core.exception.ObootException;
import org.oneboot.core.lang.Page;
import org.oneboot.core.logging.LoggerUtil;
import org.oneboot.core.mvc.base.BaseForm;
import org.oneboot.core.mvc.base.BaseVO;
import org.oneboot.core.mvc.file.CommonFileUpload;
import org.oneboot.core.mvc.file.ExcelObject;
import org.oneboot.core.mvc.file.ExcelUtil;
import org.oneboot.core.mvc.file.XlsxExcelView;
import org.oneboot.core.mybatis.base.BaseModel;
import org.oneboot.core.mybatis.base.IRepository;
import org.oneboot.core.mybatis.model.BaseSearcher;
import org.oneboot.core.result.CommonMvcResult;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;

import lombok.extern.slf4j.Slf4j;

/**
 * 一个通用的CRUD实现
 * 
 * @param <M>
 * @param <F>
 * @param <V>
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
@Slf4j
public abstract class BaseCommonController<M extends BaseModel, F extends BaseForm, V extends BaseVO>
		extends BaseController {

	/** 默认导出的最大条数控制 */
	private static final int DEFAULT_EXPORT_TOTAL_COUNT = 50000;

	protected IRepository<M, ?> repository;

	protected Class<V> voClass = currentViewClass();

	protected Class<V> modelClass = currentModelClass();

	public BaseCommonController(IRepository<M, ?> repository) {
		super();
		this.repository = repository;
	}

	@SuppressWarnings("unchecked")
	private Class<V> currentViewClass() {
		return (Class<V>) ReflectionKit.getSuperClassGenericType(getClass(), 2);
	}

	@SuppressWarnings("unchecked")
	private Class<V> currentModelClass() {
		return (Class<V>) ReflectionKit.getSuperClassGenericType(getClass(), 0);
	}

	/**
	 * 检查字段值是否已存在 GET /xx/exists
	 * 
	 * @param fieldName
	 *            字段名
	 * @param value
	 *            字段值
	 * @return
	 */
	@GetMapping("/exists")
	public CommonMvcResult<Object> doExists(@NotBlank String fieldName, @NotBlank String value) {
		CommonMvcResult<Object> map = getSucceed();
		List<String> canFieldName = putDbControlField();

		// 如果不在上面的列表中，直接报错
		if (!canFieldName.contains(fieldName)) {
			throw new ObootException(CommonErrorCode.DATA_CHCEK_FAIL, "canFieldName 未配置");
		}

		map.setCode(repository.exists(fieldName, value) + "");
		return map;
	}

	/**
	 * 显示信息 GET /:id
	 * 
	 * @param model
	 * @param id
	 *            ID
	 * @return
	 */
	@GetMapping("/{id}")
	public CommonMvcResult<Object> doShow(@PathVariable String id) {
		CommonMvcResult<Object> map = getSucceed();
		M m = repository.findById(id);
		if (null == m) {
			throw new ObootException(CommonErrorCode.DATA_SELECT_FAIL);
		}
		BaseVO vo = creactVO();
		BeanUtils.copyProperties(m, vo);
		warpTheVO(m, vo);
		vo.setId(getIdVale(m));
		map.setData(vo);
		return map;
	}

	/**
	 * 获取需要修改的信息 GET /:id/edit
	 * 
	 * @param model
	 * @param id
	 *            ID
	 */
	@GetMapping("/{id}/edit")
	public CommonMvcResult<Object> doEdit(@PathVariable String id) {
		M m = repository.findById(id);
		if (null == m) {
			throw new ObootException(CommonErrorCode.DATA_SELECT_FAIL);
		}
		BaseVO vo = creactVO();
		BeanUtils.copyProperties(m, vo);
		warpTheVO(m, vo);
		vo.setId(getIdVale(m));
		CommonMvcResult<Object> map = getSucceed();
		map.setData(vo);
		return map;
	}

	/**
	 * 批量修改字段的信息，form.field 由前端传入 PATCH /flowsupplys/:id
	 * 
	 * @param id
	 * @param form
	 * @param result
	 * @return
	 */
	@PatchMapping("/{id}/batch")
	@Transactional(rollbackFor = Exception.class)
	public CommonMvcResult<Object> doUpdateBatch(@PathVariable String id, @Valid F form, BindingResult result) {
		// 表单校验
		if (result.hasErrors()) {
			return resultIllegalFailed(result);
		}
		// 此处一定要对字段进行管控
		List<String> canFieldName = putDbControlField();

		// 如果不在上面的列表中，直接报错
		if (!canFieldName.contains(form.getField())) {
			throw new ObootException(CommonErrorCode.DATA_CHCEK_FAIL);
		}
		Object value = form.getValue();

		// 判断对id进行截取逗号分开，放到一个list里面，判断这个list 长度 大于0进行批量操作
		int updateBatch = 0;
		String[] sourceStrArray = id.split(",");
		if (sourceStrArray.length > 0) {
			List<String> list = Arrays.asList(sourceStrArray);
			// 通过多个ID，进行批量修改
			updateBatch = repository.updateBatchField(list, form.getField(), value);

		}
		if (updateBatch == 0) {
			throw new ObootException(CommonErrorCode.DATA_CHCEK_FAIL);
		}
		updateWarpVO(getWarpCacheName());
		CommonMvcResult<Object> map = getSucceed();
		return map;
	}

	/**
	 * 删除 DELETE /:id
	 * 
	 * @param model
	 * @param id
	 *            ID
	 */
	@DeleteMapping("/{id}")
	@Transactional(rollbackFor = Exception.class)
	public CommonMvcResult<Object> doDestroy(@PathVariable String id) {
		String[] ids = id.split(",");
		for (String i : ids) {
			M sendLog = repository.findById(i);
			if (null == sendLog) {
				throw new ObootException(CommonErrorCode.DATA_SELECT_FAIL);
			}
			repository.removeById(i);
		}
		updateWarpVO(getWarpCacheName());
		CommonMvcResult<Object> map = getSucceed();
		return map;
	}

	/**
	 * 文件导入操作
	 * 
	 * @param form
	 * @return
	 */
	@PostMapping(value = "/import")
	@Transactional(rollbackFor = Exception.class)
	public CommonMvcResult<Object> doImport(F form, String importType) {
		// 导入功能实现
		long start = System.currentTimeMillis();
		List<M> list = readUploadFile(form.getFileToken(), form);
		// 在这里，需要进行检查，如果有异常，需要抛出，
		for (M obj : list) {
			// 其实还有一些场景是需要忽略异常的，这里过滤掉就行
			// 如果这里需要查询数据库的话，那就会比较耗时间吧
			importCheck(obj);
		}
		repository.saveBatch(list);
		LoggerUtil.warn("批量上传总耗时：{0}ms, 总条数:{1}", System.currentTimeMillis() - start, list.size());
		CommonMvcResult<Object> map = getSucceed();
		return map;
	}

	/**
	 * 导入前置检查
	 * 
	 * @param form
	 * @return
	 */
	@GetMapping(value = "/import/check")
	public CommonMvcResult<Object> doImportCheck(F form) {
		List<M> list = readUploadFile(form.getFileToken(), form);
		for (M obj : list) {
			try {
				importCheck(obj);
			} catch (ObootException e) {
				// 检查失败，需要记录数据
				obj.setStatus("-1");
				obj.setSort(2);
				obj.setMemo(e.getMessage());
			}
		}
		// 在这里，需要检查是否全部通过
		String status = "1";
		for (BaseModel obj : list) {
			if (StringUtils.equals("-1", obj.getStatus())) {
				status = "-1";
			}
		}
		// 按提交时间降序 --Lamdba表达式，其实就是把检查失败的放在最前面的需求
		Collections.sort(list, (a, b) -> b.getSort() - a.getSort());

		CommonMvcResult<Object> map = getSucceed();
		map.setCode(status);
		map.setData(list);
		return map;
	}

	protected void isNotBlank(String text, String message) {
		if (StringUtils.isBlank(text)) {
			LoggerUtil.warn(message);
			throw new ObootException(CommonErrorCode.ILLEGAL_PARAMETERS, message);
		}
	}

	protected void importCheck(M obj) {
		// 在这里检查手机号码呀
		// ParamValidateUtils.isValidMobile(mobile)
		// 哪个不能为空呀

	}

	/**
	 * 读取上传的文档，这里需要支持csv文件格式
	 * 
	 * @param fileToken
	 * @param form
	 *            前台需要传过来的一些值，在这里好填充
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	protected List<M> readUploadFile(String fileToken, F form) {
		List<M> list = new ArrayList<>();
		// 导入功能实现
		long start = System.currentTimeMillis();
		String fileSavePath = CommonFileUpload.getFileSavePath(fileToken);
		List<Map> data = null;
		try {
			data = ExcelUtil.readExcel(fileSavePath);
		} catch (FileNotFoundException e) {
			LoggerUtil.warn("导入excel文件解析失败fileName={0},err={1}", fileToken, e.getMessage());
			throw new ObootException(CommonErrorCode.UNKNOWN_ERROR, "文件读取失败");
		}

		LoggerUtil.warn("读取上传文件耗时：{0}", System.currentTimeMillis() - start);
		convertExceData(data, list);
		return list;
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	protected List<M> convertExceData(List<Map> data, List<M> list) {
		// 在这里，可能需要去查询DB，获取一些配置数据
		for (Map<String, String> row : data) {
			String level = row.get("等级");
			BaseModel obj = new BaseModel();
			// 加入到列表中去
			// list.add(obj);
		}
		return list;
	}

	/**
	 * 导出前置检查
	 * 
	 * @param form
	 * @return
	 */
	@GetMapping(value = "/export/check")
	public CommonMvcResult<Object> doExportCheck(F form) {
		exportCheck(form);
		CommonMvcResult<Object> map = getSucceed();
		Map<String, Object> dataMap = new HashMap<String, Object>(16);
		dataMap.put("filename", getExportFileName(form) + ".xlsx");
		map.setData(dataMap);
		return map;
	}

	protected String getExportFileName(F form) {
		return "每日报表";
	}

	private void exportCheck(F form) {
		// 在这里，做前置检验，比如导出总条数，如果大于多少抛出异常
		if (getSearchData(form).getTotalCount() > DEFAULT_EXPORT_TOTAL_COUNT) {
			throw new ObootException(CommonErrorCode.DATA_CHCEK_FAIL, "导出数据大于5万条记录，请调整好条件再次操作", 1);
		}
	}

	/**
	 * 文件导出
	 * 
	 * @param form
	 * @return
	 */
	@GetMapping(value = "/export")
	public ModelAndView doExport(F form) {
		exportCheck(form);
		// 参数 MAP
		Map<String, Object> dataMap = new HashMap<String, Object>(16);
		// 在这里增加需要导出的字段
		ExcelObject<V> eo = new ExcelObject<V>("每日报表");

		String fileName = assembleExcelObj(eo);
		eo.setFilename(fileName);

		// 去DB里查询数据，注意，条件要与doIndex里保持一致
		eo.setDatas(getListAll(form));
		dataMap.put("ExcelObject", eo);
		// 增加日期格式，注意目前只支持一种格式
		XlsxExcelView view = new XlsxExcelView();
		view.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		LoggerUtil.info("本次导出 {0} 条记录", eo.getDatas().size());
		ModelAndView mv = new ModelAndView(view, dataMap);
		return mv;
	}

	/**
	 * 获取分页查询数据
	 * 
	 * @param form
	 * @return
	 */
	protected Page<V> getSearchData(F form) {
		BaseSearcher bs = getCreactBaseSearcher();
		BeanUtils.copyProperties(form, bs);
		// 分页查询数据
		Page<M> pageList = repository.searchPage(bs);
		// 此处ID是需要返回到页面的字段，请根据实际情况处理
		return copyToPage(pageList, getVoClass(), getIdFieldName());
	}

	private Page<V> getQueryData(F form) {
		BaseSearcher bs = getCreactBaseSearcher();
		BeanUtils.copyProperties(form, bs);
		// 分页查询数据
		Page<M> pageList = repository.queryPage(bs);
		// 此处ID是需要返回到页面的字段，请根据实际情况处理
		return copyToPage(pageList, getVoClass(), getIdFieldName());
	}

	private List<V> getListAll(F form) {
		List<V> list = new ArrayList<>();
		ServiceContextHolder.setOffSet(1);
		ServiceContextHolder.setPageSize(10000);
		// 得到总页数据，然后for查询
		// 分页查询数据
		Page<V> pageList = getSearchData(form);
		LoggerUtil.info("查询总数：{0}", pageList.getTotalCount());

		list.addAll(pageList.getResult());
		int totalPageSize = pageList.getTotalPageSize();

		for (int i = 2; i <= totalPageSize; i++) {
			ServiceContextHolder.setOffSet(i);
			pageList = getQueryData(form);
			list.addAll(pageList.getResult());
		}

		return list;
	}

	/**
	 * 获取VO的 class
	 * 
	 * @return
	 */
	protected Class<V> getVoClass() {
		return voClass;
	}

	/**
	 * 获取VO的实例对像
	 * 
	 * @return
	 */
	protected BaseVO creactVO() {
		try {
			return voClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("voClass.newInstance error", e);
		}
		return null;
	}

	/**
	 * 获取Model的ID值
	 * 
	 * @param m
	 * @return
	 */
	protected String getIdVale(M m) {
		String keyProperty = getIdFieldName();
		Object idVal = ReflectionKit.getFieldValue(m, keyProperty);
		if (idVal != null) {
			return idVal.toString();
		}
		return null;
	}

	/**
	 * 获取Model的ID name
	 * 
	 * @return
	 */
	protected String getIdFieldName() {
		TableInfo tableInfo = TableInfoHelper.getTableInfo(this.modelClass);
		Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
		String keyProperty = tableInfo.getKeyProperty();
		Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
		return keyProperty;
	}

	/**
	 * 获取查询类的实例对应
	 * 
	 * @return
	 */
	protected abstract BaseSearcher getCreactBaseSearcher();

	protected abstract String getWarpCacheName();

	/**
	 * 组装表格导出时的title及value对应关系，如果有需要使用到导出功能，请重写些方法
	 * 
	 * @param eo
	 * @return
	 */
	protected String assembleExcelObj(ExcelObject<V> eo) {
		eo.addTitle("orderNumber", "订单总数");
		eo.addTitle("reportDate", "结算时间");
		eo.addTitle("settleFund", "结算资金（元）");
		eo.addTitle("orderTotalFund", "订单总金额（元）");
		eo.addTitle("cpNo", "CP标识");
		return "每日报表";
	}

	/**
	 * 获取exist管控列表值，由子类来实现
	 * 
	 * @return
	 */
	protected List<String> putDbControlField() {
		List<String> list = new ArrayList<String>();
		return list;
	}
}
