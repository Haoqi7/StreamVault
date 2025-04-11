package com.flower.spirit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.flower.spirit.entity.NotifyConfigEntity;
import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.NotifyConfigDao;

import java.util.List;

@Service
public class NotifyConfigService {

	@Autowired
	private NotifyConfigDao notifyConfigDao;

	public AjaxEntity saveNotifyConfig(NotifyConfigEntity entity) {
		try {
			// 查找是否已存在配置
			List<NotifyConfigEntity> existingConfigs = notifyConfigDao.findAll();
			if (!existingConfigs.isEmpty()) {
				// 如果存在配置，更新第一个配置
				NotifyConfigEntity existingConfig = existingConfigs.get(0);
				// 保持原有ID
				entity.setId(existingConfig.getId());
			}
			// 保存配置
			notifyConfigDao.save(entity);
			return new AjaxEntity(Global.ajax_success, "保存成功", null);
		} catch (Exception e) {
			return new AjaxEntity(Global.ajax_uri_error, "保存失败: " + e.getMessage(), null);
		}
	}

	public AjaxEntity getNotifyConfig(NotifyConfigEntity entity) {
		List<NotifyConfigEntity> existingConfigs = notifyConfigDao.findAll();
		if (!existingConfigs.isEmpty()) {
			return new AjaxEntity(Global.ajax_success,"请求成功", existingConfigs.get(0));
		}
		return new AjaxEntity(Global.ajax_success,"请求成功", null);
	}
	
	public NotifyConfigEntity getData(NotifyConfigEntity entity) {
		List<NotifyConfigEntity> existingConfigs = notifyConfigDao.findAll();
		if (!existingConfigs.isEmpty()) {
			return existingConfigs.get(0);
		}
		return null;
	}

}
