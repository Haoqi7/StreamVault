package com.flower.spirit.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.flower.spirit.entity.ProcessHistoryEntity;




@Repository
@Transactional
public interface ProcessHistoryDao extends PagingAndSortingRepository<ProcessHistoryEntity, Integer>, JpaSpecificationExecutor<ProcessHistoryEntity>{

	
	public List<ProcessHistoryEntity> findAll();

	

}
