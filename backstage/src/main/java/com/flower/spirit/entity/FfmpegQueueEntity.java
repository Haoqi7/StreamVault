package com.flower.spirit.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "biz_ffmpeg_queue")
public class FfmpegQueueEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7997224550086109374L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="biz_ffmpeg_queue")
	@TableGenerator(name = "biz_ffmpeg_queue", allocationSize = 1, table = "seq_common", pkColumnName = "seq_id", valueColumnName = "seq_count")
    private Integer id;
	
	private String videoid;
	
	private String videoname;
	
	private String videostatus;
	
	private String audiostatus;
	
	private String pendingfolder;
	
	private String filepath;
	
	private String status;
	
	private String createtime;

	public Integer getId() {
		return id;
	}

	public String getVideoid() {
		return videoid;
	}

	public String getVideoname() {
		return videoname;
	}

	public String getVideostatus() {
		return videostatus;
	}

	public String getAudiostatus() {
		return audiostatus;
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

	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}

	public void setVideoname(String videoname) {
		this.videoname = videoname;
	}

	public void setVideostatus(String videostatus) {
		this.videostatus = videostatus;
	}

	public void setAudiostatus(String audiostatus) {
		this.audiostatus = audiostatus;
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

	public String getPendingfolder() {
		return pendingfolder;
	}

	public void setPendingfolder(String pendingfolder) {
		this.pendingfolder = pendingfolder;
	}

	
	


}
