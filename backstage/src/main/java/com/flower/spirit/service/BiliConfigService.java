package com.flower.spirit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.BiliConfigDao;
import com.flower.spirit.entity.BiliConfigEntity;
import com.flower.spirit.utils.HttpUtil;

@Service
public class BiliConfigService {
	
	@Autowired
	private BiliConfigDao BiliConfigDao;
	
	public BiliConfigEntity getData() {
		List<BiliConfigEntity> findAll = BiliConfigDao.findAll();
		if(findAll.size() == 0) {
			BiliConfigEntity biliConfigEntity = new BiliConfigEntity();
			BiliConfigDao.save(biliConfigEntity);
			return biliConfigEntity;
		}
		return findAll.get(0);
	}

	/**
	 * 修改配置
	 * @param entity
	 * @return
	 */
	public AjaxEntity updateBiliConfig(BiliConfigEntity entity) {
		BiliConfigDao.save(entity);
		Global.bilicookies = entity.getBilicookies();
		if(null != entity.getBigmember() && entity.getBigmember().equals("是")) {
			Global.bilimember= true;
		}
		if(null != entity.getBitstream() && !"".equals(entity.getBitstream())) {
			Global.bilibitstream= entity.getBitstream();
		}
		return new AjaxEntity(Global.ajax_success, "操作成功", entity);
	}

	/**
	 * 获取登录二维码
	 * @return
	 */
	public AjaxEntity getBiliCode() {
		String httpGet = HttpUtil.httpGet("https://passport.bilibili.com/x/passport-login/web/qrcode/generate", "UTF-8");
		
		return new AjaxEntity(Global.ajax_success,"请求成功", JSONObject.parseObject(httpGet));
	}

	/**
	 * 检测登录状态并设置ck
	 * @param qrcodekey
	 * @return
	 */
	public AjaxEntity checkBiliLogin(String qrcodekey) {
		String httpGet = HttpUtil.httpGetBypoll("https://passport.bilibili.com/x/passport-login/web/qrcode/poll?qrcode_key="+qrcodekey, "UTF-8");
		if(httpGet != null) {
			List<BiliConfigEntity> findAll = BiliConfigDao.findAll();
			if(findAll.size() == 0) {
				BiliConfigEntity biliConfigEntity = new BiliConfigEntity();
				biliConfigEntity.setBilicookies(httpGet);
				Global.bilicookies = httpGet;
				BiliConfigDao.save(biliConfigEntity);
				return new AjaxEntity(Global.ajax_success, "操作成功", biliConfigEntity);
			}else {
				BiliConfigEntity biliConfigEntity = findAll.get(0);
				biliConfigEntity.setBilicookies(httpGet);
				BiliConfigDao.save(biliConfigEntity);
				Global.bilicookies =httpGet;
				return new AjaxEntity(Global.ajax_success, "操作成功", biliConfigEntity);
			}
		}else {
			return new AjaxEntity(Global.ajax_uri_error,"操作失败,请重新登录", null);
		}
		
	}

}
