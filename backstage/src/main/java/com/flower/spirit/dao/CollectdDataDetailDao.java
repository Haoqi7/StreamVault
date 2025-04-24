package com.flower.spirit.dao;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.flower.spirit.entity.CollectDataDetailEntity;




@Repository
@Transactional
public interface CollectdDataDetailDao extends PagingAndSortingRepository<CollectDataDetailEntity, Integer>, JpaSpecificationExecutor<CollectDataDetailEntity>{

	
	public List<CollectDataDetailEntity> findAll();

	public void deleteByDataid(Integer dataid);

	public List<CollectDataDetailEntity> findByVideoidAndDataid(String videoid, String string);

	

}
