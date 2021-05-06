package org.oneboot.core.mvc;

import java.util.Map;

import org.oneboot.core.cache.local.UploadWarpVoEvent;
import org.oneboot.core.cache.local.WarpTheVoService;
import org.oneboot.core.enums.EnumObject;
import org.oneboot.core.mybatis.base.BaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;

/**
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public class BaseController extends ObootBaseController {

	@Autowired
	protected WarpTheVoService warpTheVoService;

	protected void updateWarpVO(String key) {
		applicationContext.publishEvent(new UploadWarpVoEvent(key));
	}

	protected void publishEvent(ApplicationEvent event) {
		applicationContext.publishEvent(event);
	}

	protected String getWarpName(String fieldValue, String cacheName) {
		Map<String, EnumObject> cacheAllMap = warpTheVoService.getMap(cacheName);
		EnumObject eo = cacheAllMap.get(fieldValue);
		if (eo != null) {
			return eo.getName();
		}
		return fieldValue;
	}

	

	/**
	 * 设置新建者信息
	 * 
	 * @param base
	 */
	protected void setCreateInfo(BaseModel base) {
		base.setTntInstId("tntinst");
		base.setCreator("admin");
		base.setUpdater("admin");
	}

	/**
	 * 设置新建信息
	 * 
	 * @param base
	 */
	protected void setModifyInfo(BaseModel base) {
		base.setUpdater("admin");
	}
}
