package com.flower.spirit.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.flower.spirit.common.DataEntity;

@Entity
@Table(name = "biz_collect_data_detail")
public class CollectDataDetailEntity extends DataEntity<CollectDataDetailEntity> implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2752642646580560817L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="biz_collect_data_detail")
	@TableGenerator(name = "biz_collect_data_detail", allocationSize = 1, table = "seq_common", pkColumnName = "seq_id", valueColumnName = "seq_count")
    private Integer id;
	
	private Integer dataid;
	
	private String videoid;
	
	private String videoname;
	
	private String originaladdress;
	
	private String status;
	
	private String createtime;
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDataid() {
		return dataid;
	}

	public void setDataid(Integer dataid) {
		this.dataid = dataid;
	}

	public String getVideoid() {
		return videoid;
	}

	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}

	
	public String getVideoname() {
		return videoname;
	}

	public void setVideoname(String videoname) {
		this.videoname = videoname;
	}

	public String getOriginaladdress() {
		return originaladdress;
	}

	public void setOriginaladdress(String originaladdress) {
		this.originaladdress = originaladdress;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	
	
	
	
	

}
