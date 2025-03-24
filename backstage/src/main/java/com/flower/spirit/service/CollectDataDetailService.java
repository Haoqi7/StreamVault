package com.flower.spirit.service;

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
import com.flower.spirit.dao.CollectdDataDetailDao;
import com.flower.spirit.entity.CollectDataDetailEntity;

@Service
public class CollectDataDetailService {
	
	
	@Autowired
	private CollectdDataDetailDao collectdDataDetailDao;

	public CollectDataDetailEntity save(CollectDataDetailEntity collectDataDetailEntity) {
		return collectdDataDetailDao.save(collectDataDetailEntity);
	}

	@SuppressWarnings("serial")
	public AjaxEntity findPage(CollectDataDetailEntity res) {
		PageRequest of= PageRequest.of(res.getPageNo(), res.getPageSize());
		Specification<CollectDataDetailEntity> specification = new Specification<CollectDataDetailEntity>() {

			@Override
			public Predicate toPredicate(Root<CollectDataDetailEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				Predicate predicate=criteriaBuilder.conjunction();
				CollectDataDetailEntity seachDate = (CollectDataDetailEntity) res;
				if(seachDate != null && seachDate.getDataid() != null) {
					predicate.getExpressions().add(criteriaBuilder.equal(root.get("dataid"), seachDate.getDataid()));
				}
				if(seachDate != null && seachDate.getStatus() != null) {
					predicate.getExpressions().add(criteriaBuilder.like(root.get("status"), "%"+seachDate.getStatus()+"%"));
				}

				query.orderBy(criteriaBuilder.desc(root.get("id")));
				return predicate;
			}};
			
			Page<CollectDataDetailEntity> findAll = collectdDataDetailDao.findAll(specification,of);
			return new AjaxEntity(Global.ajax_success, "数据获取成功", findAll);
	}
	public void deleteDataid(Integer dataid) {
		collectdDataDetailDao.deleteByDataid(dataid);
	}

}
