package com.flower.spirit.service;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.common.RequestEntity;
import com.flower.spirit.config.AppConfig;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.DownloaderDao;
import com.flower.spirit.entity.DownloaderEntity;
@Service
public class DownloaderService {
	
	private static Logger logger = LoggerFactory.getLogger(DownloaderService.class);
	
	@Autowired
	private DownloaderDao  downloaderDao;
	
	
	@Autowired
	@Lazy
	private AppConfig appConfig;

	public void renovate() {
		List<DownloaderEntity> list =  downloaderDao.findAll();
		for(DownloaderEntity data :list) {
			if(data.getStatus().equals("1")) {
				if(data.getDownloadertype().equals("aria2")) {
					//目前仅加载aria2
					String a2_link = data.getDownloaderlink()+":"+data.getDownloaderport()+"/jsonrpc";
					Global.a2_link =a2_link;
					Global.down_path =data.getDownloadpath();
					Global.a2_token =data.getToken();
					Global.downtype ="a2";
					logger.info("已加载默认下载器"+a2_link);
				}
				if(data.getDownloadertype().equals("http")) {
					//基础http 下载器
					Global.down_path =data.getDownloadpath();
					Global.downtype ="http";
					logger.info("已使用基础http下载器");
				}
			}
		}
		
	}
	
	
	@SuppressWarnings("serial")
	public AjaxEntity finddownLoaderList(RequestEntity res) {
		int page = res.getPage()==  null ?0:res.getPage();
		int limit = res.getLimit() == null?15:res.getLimit();
		Pageable pageable = PageRequest.of(page,limit);
		Specification<DownloaderEntity> specification = new Specification<DownloaderEntity>() {

			@Override
			public Predicate toPredicate(Root<DownloaderEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				Predicate predicate=criteriaBuilder.conjunction();
				return predicate;
			}};
		
		Page<DownloaderEntity> findAll = downloaderDao.findAll(specification,pageable);
		return new AjaxEntity(Global.ajax_success, "数据获取成功", findAll);
	}

	public AjaxEntity delDownLoader(DownloaderEntity downloaderEntity) {
		downloaderDao.deleteById(downloaderEntity.getId());
		return new AjaxEntity(Global.ajax_success, Global.ajax_option_success, null);
	}

	public AjaxEntity addDownLoader(DownloaderEntity downloaderEntity) {
		//判断是否已经有启用的了如果本次还是启用 禁止添加
		if(downloaderEntity.getStatus().equals("1")) {
			DownloaderEntity data =downloaderDao.findByStatus("1");
			if(data!= null  && downloaderEntity.getId() == null) {
				return new AjaxEntity(Global.ajax_uri_error, "都已经添加一个启用的下载器了!你还想用两个下载器同时下载！这个要添加为禁用~", null);
			}
			if(data!= null  && downloaderEntity.getId() != null  && !downloaderEntity.getId().equals(data.getId())) {
				return new AjaxEntity(Global.ajax_uri_error, "都已经添加一个启用的下载器了!你还想用两个下载器同时下载！这个要添加为禁用~", null);
			}
		}
		downloaderDao.save(downloaderEntity);
		appConfig.init();
		return new AjaxEntity(Global.ajax_success, Global.ajax_option_success, null);
	}

	public AjaxEntity getDownLoader(DownloaderEntity downloaderEntity) {
		return new AjaxEntity(Global.ajax_success, "数据获取成功", downloaderDao.findById(downloaderEntity.getId()));
	}

}
