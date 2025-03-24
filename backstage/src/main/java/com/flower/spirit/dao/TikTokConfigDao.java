package com.flower.spirit.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.flower.spirit.entity.TikTokConfigEntity;




@Repository
@Transactional
public interface TikTokConfigDao extends PagingAndSortingRepository<TikTokConfigEntity, Integer>, JpaSpecificationExecutor<TikTokConfigEntity>{

	
	public List<TikTokConfigEntity> findAll();

	

}
