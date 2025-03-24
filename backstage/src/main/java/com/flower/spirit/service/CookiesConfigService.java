package com.flower.spirit.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.CookiesConfigDao;
import com.flower.spirit.entity.CookiesConfigEntity;


@Service
public class CookiesConfigService {
	
	@Autowired
	private CookiesConfigDao cookiesConfigDao;

	public CookiesConfigEntity getData() {
		List<CookiesConfigEntity> findAll = cookiesConfigDao.findAll();
		if(findAll.size() == 0) {
			CookiesConfigEntity cookiesConfigEntity = new CookiesConfigEntity();
			cookiesConfigDao.save(cookiesConfigEntity);
			return cookiesConfigEntity;
		}
		return findAll.get(0);
		
	}

	public AjaxEntity updateCookie(CookiesConfigEntity entity) {
		//CookiesConfigEntity cookiesConfigEntity = cookiesConfigDao.findById(entity.getId()).get();
		Global.youtubecookies = entity.getYoutubecookies();
		Global.twittercookies = entity.getTwittercookies();
		cookiesConfigDao.save(entity);
		return new AjaxEntity(Global.ajax_success, "更新成功", null);

	}

}
