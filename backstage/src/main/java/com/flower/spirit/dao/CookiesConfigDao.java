package com.flower.spirit.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.flower.spirit.entity.CookiesConfigEntity;

@Repository
@Transactional
public interface CookiesConfigDao extends PagingAndSortingRepository<CookiesConfigEntity, Integer>, JpaSpecificationExecutor<CookiesConfigEntity> {

	public List<CookiesConfigEntity> findAll();
	
}
