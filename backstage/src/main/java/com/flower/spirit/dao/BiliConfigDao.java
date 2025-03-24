package com.flower.spirit.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.flower.spirit.entity.BiliConfigEntity;




@Repository
@Transactional
public interface BiliConfigDao extends PagingAndSortingRepository<BiliConfigEntity, Integer>, JpaSpecificationExecutor<BiliConfigEntity>{

	
	public List<BiliConfigEntity> findAll();

	

}
