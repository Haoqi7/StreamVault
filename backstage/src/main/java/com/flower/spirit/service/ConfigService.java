package com.flower.spirit.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.AppConfig;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.ConfigDao;
import com.flower.spirit.entity.ConfigEntity;

@Service
public class ConfigService {
	
	@Autowired
	private ConfigDao  configDao;
	
	private Logger logger = LoggerFactory.getLogger(ConfigService.class);

	public ConfigEntity getData() {
		List<ConfigEntity> list =  configDao.findAll();
		return list.get(0);
	}

	public AjaxEntity saveConfig(ConfigEntity configEntity) {
		configDao.save(configEntity);
		Global.apptoken =configEntity.getApptoken();
		if(configEntity.getGeneratenfo()!= null && configEntity.getGeneratenfo().equals("1")) {
			Global.getGeneratenfo =  true;
		}
		if (configEntity.getAgenttype() != null && !configEntity.getAgenttype().trim().isEmpty() &&
				configEntity.getAgentaddress() != null && !configEntity.getAgentaddress().trim().isEmpty() &&
						configEntity.getAgentport() != null && !configEntity.getAgentport().trim().isEmpty()) {
			Global.proxyinfo = AppConfig.buildProxyArgument(configEntity);
			logger.info("已启动yt-dlp网络代理,代理地址:"+Global.proxyinfo); 
		}else {
			Global.proxyinfo=null;
		}
		return new AjaxEntity(Global.ajax_option_success, "操作成功", configEntity);
	}

}
