package com.flower.spirit.service;

import java.io.File;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.VideoDataDao;
import com.flower.spirit.entity.VideoDataEntity;
import com.flower.spirit.utils.CommandUtil;
import com.flower.spirit.utils.FileUtils;
import com.flower.spirit.utils.StringUtil;


@Service
public class VideoDataService {
	
    @Value("${file.save}")
    private String savefile;
    
    @Value("${file.save.path}")
    private String uploadRealPath;
	
	
	@Autowired
	private VideoDataDao videoDataDao;
	
	public List<VideoDataEntity> findByVideoid(String videoid){
		return videoDataDao.findByVideoid(videoid);
	}
	

	@SuppressWarnings("serial")
	public AjaxEntity findPage(VideoDataEntity res) {
		PageRequest of= PageRequest.of(res.getPageNo(), res.getPageSize());
		Specification<VideoDataEntity> specification = new Specification<VideoDataEntity>() {

			@Override
			public Predicate toPredicate(Root<VideoDataEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				Predicate predicate=criteriaBuilder.conjunction();
				VideoDataEntity seachDate = (VideoDataEntity) res;
				if(seachDate != null && StringUtil.isString(seachDate.getVideodesc())) {
					predicate.getExpressions().add(criteriaBuilder.like(root.get("videodesc"), "%"+seachDate.getVideodesc()+"%"));
				}
				if(seachDate != null && StringUtil.isString(seachDate.getVideoname())) {
					predicate.getExpressions().add(criteriaBuilder.like(root.get("videoname"), "%"+seachDate.getVideoname()+"%"));
				}
				if(seachDate != null && StringUtil.isString(seachDate.getVideoplatform())) {
					predicate.getExpressions().add(criteriaBuilder.like(root.get("videoplatform"), "%"+seachDate.getVideoplatform()+"%"));
				}
				query.orderBy(criteriaBuilder.desc(root.get("id")));
				return predicate;
			}};
			
			Page<VideoDataEntity> findAll = videoDataDao.findAll(specification,of);
			return new AjaxEntity(Global.ajax_success, "数据获取成功", findAll);
	}

	/**
	 * 删除
	 * @param downloaderEntity
	 * @return
	 */
	public AjaxEntity deleteVideoData(VideoDataEntity data) {
		//删除也要删除资源
		Optional<VideoDataEntity> findById = videoDataDao.findById(data.getId());
		if(findById.isPresent()) {
			VideoDataEntity videoDataEntity = findById.get();
			//新版 优化 此处可以修改为直接删除文件夹
//			FileUtils.deleteFile(videoDataEntity.getVideoaddr()); 
//			FileUtils.deleteParentDirectorie(videoDataEntity.getVideoaddr());  //直接删除上级文件夹
			CommandUtil.deleteDirectory(new File(videoDataEntity.getVideoaddr()).getParentFile().getPath());//更换系统级原生删除方法
//			FileUtils.deleteFile(videoDataEntity.getVideocover().replace(savefile, uploadRealPath));
			videoDataDao.deleteById(data.getId());
		}
		return new AjaxEntity(Global.ajax_success, "操作成功", null);
	}

	/**
	 * 更新
	 * @param data
	 * @return
	 */
	public AjaxEntity updateVideoData(VideoDataEntity data) {
		Optional<VideoDataEntity> findById = videoDataDao.findById(data.getId());
		if(findById.isPresent()) {
			VideoDataEntity videoDataEntity = findById.get();
			videoDataEntity.setVideoname(data.getVideoname());
			videoDataEntity.setVideodesc(data.getVideodesc());
			videoDataEntity.setVideoplatform(data.getVideoplatform());
			videoDataDao.save(videoDataEntity);
		}
		return new AjaxEntity(Global.ajax_success, "操作成功", null);
	}

}
