package com.flower.spirit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import javax.persistence.*;

import com.flower.spirit.common.DataEntity;

@Entity
@Table(name = "biz_video_mix")
public class VideoMixEntity extends DataEntity<VideoMixEntity> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3434684139072539263L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "biz_video_mix")
	@TableGenerator(name = "biz_video_mix", allocationSize = 1, table = "seq_common", pkColumnName = "seq_id", valueColumnName = "seq_count")
	private Integer id;

	@Column(name = "mix_name", nullable = false)
	private String mixName; // 混剪名称

	@Column(name = "status")
	private String status; // 状态：待处理、处理中、已完成、失败

	@Column(name = "output_path")
	private String outputPath; // 输出文件路径

	@Column(name = "thumbnail_path")
	private String thumbnailPath; // 缩略图路径

	@Column(name = "duration")
	private Integer duration; // 总时长(秒)

	@Column(name = "create_time")
	private Date createTime; // 创建时间

	@Column(name = "update_time")
	private Date updateTime; // 更新时间

	@Transient
	private List<VideoMixSegmentEntity> segments; // 视频片段列表

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMixName() {
		return mixName;
	}

	public void setMixName(String mixName) {
		this.mixName = mixName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public List<VideoMixSegmentEntity> getSegments() {
		return segments;
	}

	public void setSegments(List<VideoMixSegmentEntity> segments) {
		this.segments = segments;
	}

}
