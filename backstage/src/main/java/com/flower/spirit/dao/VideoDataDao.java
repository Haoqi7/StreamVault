package com.flower.spirit.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.flower.spirit.entity.VideoDataEntity;


@Repository
@Transactional
public interface VideoDataDao extends PagingAndSortingRepository<VideoDataEntity, Integer>, JpaSpecificationExecutor<VideoDataEntity>{

	List<VideoDataEntity> findByVideoid(String videoid);

	List<VideoDataEntity> findByVideoidAndVideoplatform(String id, String platform);

	

}
