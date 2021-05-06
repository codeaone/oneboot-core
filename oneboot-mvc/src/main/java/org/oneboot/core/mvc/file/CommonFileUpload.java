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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.oneboot.core.mvc.ObootBaseController;
import org.oneboot.core.result.CommonMvcResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用文件上传
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public class CommonFileUpload extends ObootBaseController {

	public static String UPLOAD_LOCATION = "C:/mytemp/";

	public static Map<String, String> UPLOAD_FILE_TOKEN_MAP = new HashMap<>(16);

	@Autowired
	FileValidator fileValidator;

	@Autowired
	MultiFileValidator multiFileValidator;

	public static String getFileSavePath(String fileToken) {
		return UPLOAD_FILE_TOKEN_MAP.get(fileToken);
	}

	@InitBinder("fileBucket")
	protected void initBinderFileBucket(WebDataBinder binder) {
		binder.setValidator(fileValidator);
	}

	@InitBinder("multiFileBucket")
	protected void initBinderMultiFileBucket(WebDataBinder binder) {
		binder.setValidator(multiFileValidator);
	}

	// @PostMapping("/upload")
	public CommonMvcResult<Object> singleFileUpload(@Valid FileBucket fileBucket, BindingResult result)
			throws IOException {

		// 表单校验
		if (result.hasErrors()) {
			return resultIllegalFailed(result);
		}

		MultipartFile multipartFile = fileBucket.getFile();
		File filePath = new File(UPLOAD_LOCATION);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		// Now do something with file...
		FileCopyUtils.copy(fileBucket.getFile().getBytes(),
				new File(UPLOAD_LOCATION + fileBucket.getFile().getOriginalFilename()));
		String fileName = multipartFile.getOriginalFilename();
		CommonMvcResult<Object> map = getSucceed();
		Map<String, Object> dataMap = new HashMap<String, Object>(16);
		dataMap.put("filename", fileName);
		map.setData(dataMap);
		return map;
	}

	@PostMapping("/upload/multi")
	public CommonMvcResult<Object> multiFileUpload(@Valid MultiFileBucket multiFileBucket, BindingResult result)
			throws IOException {

		// 表单校验
		if (result.hasErrors()) {
			return resultIllegalFailed(result);
		}

		File filePath = new File(UPLOAD_LOCATION);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		List<String> fileNames = new ArrayList<String>();

		// Now do something with file...
		for (FileBucket bucket : multiFileBucket.getFiles()) {
			FileCopyUtils.copy(bucket.getFile().getBytes(),
					new File(UPLOAD_LOCATION + bucket.getFile().getOriginalFilename()));
			fileNames.add(bucket.getFile().getOriginalFilename());
		}

		CommonMvcResult<Object> map = getSucceed();
		Map<String, Object> dataMap = new HashMap<String, Object>(16);
		dataMap.put("fileNames", fileNames);
		map.setData(dataMap);
		return map;
	}
}
