package com.flower.spirit.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.flower.spirit.entity.VideoMixSegmentEntity;




@Repository
@Transactional
public interface VideoMixSegmentDao extends PagingAndSortingRepository<VideoMixSegmentEntity, Integer>, JpaSpecificationExecutor<VideoMixSegmentEntity>{

	
	public List<VideoMixSegmentEntity> findAll();

	public List<VideoMixSegmentEntity> findByVideomixidOrderBySegmentNoAsc(Integer id);

	public void deleteByVideomixid(Integer id);


	

}
