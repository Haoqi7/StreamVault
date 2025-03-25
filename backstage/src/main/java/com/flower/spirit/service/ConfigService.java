package com.flower.spirit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.ConfigDao;
import com.flower.spirit.entity.ConfigEntity;

@Service
public class ConfigService {
	
	@Autowired
	private ConfigDao  configDao;

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
		return new AjaxEntity(Global.ajax_option_success, "操作成功", configEntity);
	}

}
