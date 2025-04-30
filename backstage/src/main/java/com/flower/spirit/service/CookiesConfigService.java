package com.flower.spirit.service;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.CookiesConfigDao;
import com.flower.spirit.entity.CookiesConfigEntity;
import com.flower.spirit.entity.CookiesRequestEntity;

@Service
public class CookiesConfigService {

	@Autowired
	private CookiesConfigDao cookiesConfigDao;

	public CookiesConfigEntity getData() {
		List<CookiesConfigEntity> findAll = cookiesConfigDao.findAll();
		if (findAll.size() == 0) {
			CookiesConfigEntity cookiesConfigEntity = new CookiesConfigEntity();
			cookiesConfigDao.save(cookiesConfigEntity);
			return cookiesConfigEntity;
		}
		return findAll.get(0);

	}

	public AjaxEntity updateCookie(CookiesConfigEntity entity) {
		// CookiesConfigEntity cookiesConfigEntity =
		// cookiesConfigDao.findById(entity.getId()).get();
		Global.cookie_manage = entity;
		cookiesConfigDao.save(entity);
		return new AjaxEntity(Global.ajax_success, "更新成功", null);

	}

	// 向本地写入cookie

	public AjaxEntity writeCookies(CookiesRequestEntity cookiesRequestEntity) {
		try {
			String apppath = Global.apppath;
			String platform = cookiesRequestEntity.getPlatform();
			String cookies = cookiesRequestEntity.getCookies();

			// 确保cookies目录存在
			File cookieDir = new File(apppath + "/cookies");
			if (!cookieDir.exists()) {
				cookieDir.mkdirs();
			}

			// 写入cookie文件
			File cookieFile = new File(cookieDir, platform + ".txt");
			try (FileWriter writer = new FileWriter(cookieFile)) {
				writer.write(cookies);
			}

			return new AjaxEntity(Global.ajax_success, "Cookie保存成功", null);
		} catch (Exception e) {
			return new AjaxEntity(Global.ajax_uri_error, "Cookie保存失败: " + e.getMessage(), null);
		}
	}

	// 检查本地cookie状态
	public AjaxEntity checkCookies() {
		try {
			String apppath = Global.apppath;
			File cookieDir = new File(apppath + "/cookies");

			// 检查cookies目录
			if (!cookieDir.exists()) {
				return new AjaxEntity(Global.ajax_success, "cookies目录不存在", false);
			}

			// 检查文件状态
			Map<String, Boolean> status = new HashMap<>();

			if (cookieDir.exists() && cookieDir.isDirectory()) {
				File[] files = cookieDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
				if (files != null) {
					for (File file : files) {
						String platform = file.getName().replace(".txt", "");
						status.put(platform, true);
					}
				}
			}

			return new AjaxEntity(Global.ajax_success, "检查完成", status);
		} catch (Exception e) {
			return new AjaxEntity(Global.ajax_uri_error, "检查失败: " + e.getMessage(), null);
		}
	}

}
