package com.flower.spirit.service;

import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.ProcessHistoryDao;
import com.flower.spirit.entity.ProcessHistoryEntity;
import com.flower.spirit.utils.DateUtils;
import com.flower.spirit.utils.StringUtil;

@Service
public class ProcessHistoryService {
	
	@Autowired
	private ProcessHistoryDao processHistoryDao;
	
	public ProcessHistoryEntity saveProcess(Integer id,String originaladdress,String videoplatform) {
		if(Global.openprocesshistory) {
			Integer ids = id==null?null:id;
			String status =id==null?"已提交未执行":"执行完毕";
			ProcessHistoryEntity processHistoryEntity = new ProcessHistoryEntity(ids, originaladdress, videoplatform,status);
			if(id != null) {
				processHistoryEntity.setCreatetime(DateUtils.formatDateTime(new Date()));
			}
			return processHistoryDao.save(processHistoryEntity);
		}
		return new ProcessHistoryEntity();
	}

	 
	@SuppressWarnings("serial")
	public AjaxEntity findPage(ProcessHistoryEntity res) {
		PageRequest of= PageRequest.of(res.getPageNo(), res.getPageSize());
		Specification<ProcessHistoryEntity> specification = new Specification<ProcessHistoryEntity>() {

			@Override
			public Predicate toPredicate(Root<ProcessHistoryEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				Predicate predicate=criteriaBuilder.conjunction();
				ProcessHistoryEntity seachDate = (ProcessHistoryEntity) res;
				if(seachDate != null && StringUtil.isString(seachDate.getOriginaladdress())) {
					predicate.getExpressions().add(criteriaBuilder.like(root.get("originaladdress"), "%"+seachDate.getOriginaladdress()+"%"));
				}
				if(seachDate != null && StringUtil.isString(seachDate.getVideoplatform())) {
					predicate.getExpressions().add(criteriaBuilder.like(root.get("videoplatform"), "%"+seachDate.getVideoplatform()+"%"));
				}
				if(seachDate != null && StringUtil.isString(seachDate.getStatus())) {
					predicate.getExpressions().add(criteriaBuilder.like(root.get("status"), "%"+seachDate.getStatus()+"%"));
				}
				query.orderBy(criteriaBuilder.desc(root.get("id")));
				return predicate;
			}};
			
			Page<ProcessHistoryEntity> findAll = processHistoryDao.findAll(specification,of);
		
		return new AjaxEntity(Global.ajax_success, "数据获取成功", findAll);
	}


	public AjaxEntity deleteProcessHistoryData(ProcessHistoryEntity processHistoryEntity) {
		processHistoryDao.deleteById(processHistoryEntity.getId());
		return new AjaxEntity(Global.ajax_success, "操作成功", null);
	}
	
	
}
