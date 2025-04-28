package com.flower.spirit.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.flower.spirit.entity.VideoMixEntity;

@Repository
@Transactional
public interface VideoMixDao extends PagingAndSortingRepository<VideoMixEntity, Integer>, JpaSpecificationExecutor<VideoMixEntity>{

	
	public List<VideoMixEntity> findAll();

	

}
