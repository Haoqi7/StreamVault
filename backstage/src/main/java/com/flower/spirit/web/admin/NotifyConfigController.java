package com.flower.spirit.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.entity.NotifyConfigEntity;
import com.flower.spirit.service.NotifyConfigService;

@RestController
@RequestMapping("/admin/api")
public class NotifyConfigController {
	
	@Autowired
	private NotifyConfigService notifyConfigService;
	
	
	@PostMapping(value = "/saveNotifyConfig")
	public AjaxEntity saveNotifyConfig(NotifyConfigEntity entity) {
		return notifyConfigService.saveNotifyConfig(entity);
	}
	
	
	@GetMapping(value = "/getNotifyConfig")
	public AjaxEntity getNotifyConfig(NotifyConfigEntity entity) {
		return notifyConfigService.getNotifyConfig(entity);
	}

}
