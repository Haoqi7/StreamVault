package com.flower.spirit.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.flower.spirit.entity.ConfigEntity;
import com.flower.spirit.entity.NotifyConfigEntity;

@Repository
@Transactional
public interface NotifyConfigDao extends PagingAndSortingRepository<NotifyConfigEntity, Integer>, JpaSpecificationExecutor<NotifyConfigEntity>  {

	public List<NotifyConfigEntity> findAll();
}
