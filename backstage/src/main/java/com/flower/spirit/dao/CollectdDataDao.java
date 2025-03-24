package com.flower.spirit.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.flower.spirit.entity.CollectDataEntity;




@Repository
@Transactional
public interface CollectdDataDao extends PagingAndSortingRepository<CollectDataEntity, Integer>, JpaSpecificationExecutor<CollectDataEntity>{

	
	public List<CollectDataEntity> findAll();

	public List<CollectDataEntity> findByMonitoring(String string);

	

}
