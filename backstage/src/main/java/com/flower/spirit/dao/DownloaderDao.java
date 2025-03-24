package com.flower.spirit.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.flower.spirit.entity.DownloaderEntity;

@Repository
@Transactional
public interface DownloaderDao extends PagingAndSortingRepository<DownloaderEntity, Integer>, JpaSpecificationExecutor<DownloaderEntity> {

	DownloaderEntity findByStatus(String string);

	
	List<DownloaderEntity> findAll();
}
