package com.flower.spirit.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/*
 * 合并完成后  此表数据物理删除
 * 
 * **/
@Entity
@Table(name = "biz_ffmpeg_queue_data")
public class FfmpegQueueDataEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8387413168528989674L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="biz_ffmpeg_queue_data")
	@TableGenerator(name = "biz_ffmpeg_queue_data", allocationSize = 1, table = "seq_common", pkColumnName = "seq_id", valueColumnName = "seq_count")
    private Integer id;
	
	private Integer queueid;
	
	private String taskid;
	
	private String filepath;
	
	private String filetype;
	
	private String status;
	
	private String createtime;

	public Integer getId() {
		return id;
	}

	public String getTaskid() {
		return taskid;
	}

	public String getStatus() {
		return status;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public Integer getQueueid() {
		return queueid;
	}

	public void setQueueid(Integer queueid) {
		this.queueid = queueid;
	}
	
	
	


}
