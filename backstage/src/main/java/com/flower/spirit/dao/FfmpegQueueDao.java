package com.flower.spirit.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.flower.spirit.entity.FfmpegQueueEntity;


@Repository
@Transactional
public interface FfmpegQueueDao  extends PagingAndSortingRepository<FfmpegQueueEntity, Integer>, JpaSpecificationExecutor<FfmpegQueueEntity>  {

	List<FfmpegQueueEntity> findByVideostatusAndAudiostatus(String string, String string2);

	List<FfmpegQueueEntity> findByVideostatusAndAudiostatusAndStatus(String string, String string2, String string3);

}
