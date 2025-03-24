package com.flower.spirit.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.flower.spirit.entity.FfmpegQueueDataEntity;



@Repository
@Transactional
public interface FfmpegQueueDataDao extends PagingAndSortingRepository<FfmpegQueueDataEntity, Integer>, JpaSpecificationExecutor<FfmpegQueueDataEntity> {

	List<FfmpegQueueDataEntity> findByStatus(String string);

	List<FfmpegQueueDataEntity> findByQueueid(Integer id);

}
