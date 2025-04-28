package com.flower.spirit.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.flower.spirit.common.DataEntity;

@Entity
@Table(name = "biz_video_mix_segment")
public class VideoMixSegmentEntity extends DataEntity<VideoMixSegmentEntity> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8960165877665207384L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "biz_video_mix_segment")
	@TableGenerator(name = "biz_video_mix_segment", allocationSize = 1, table = "seq_common", pkColumnName = "seq_id", valueColumnName = "seq_count")
	private Integer id;

	private Integer videomixid; // 所属混剪

	private Integer videoid; // 关联的视频

	private String videoname;

	@Column(name = "segment_no", nullable = false)
	private Integer segmentNo; // 片段序号

	@Column(name = "start_time", nullable = false)
	private Integer startTime; // 开始时间(秒)

	@Column(name = "end_time", nullable = false)
	private Integer endTime; // 结束时间(秒)

	@Column(name = "duration")
	private Integer duration; // 片段时长(秒)

	@Column(name = "transition_type")
	private String transitionType; // 转场效果类型

	@Column(name = "transition_duration")
	private Integer transitionDuration; // 转场时长(秒)

	@Column(name = "create_time")
	private Date createTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getVideomixid() {
		return videomixid;
	}

	public void setVideomixid(Integer videomixid) {
		this.videomixid = videomixid;
	}

	public Integer getVideoid() {
		return videoid;
	}

	public void setVideoid(Integer videoid) {
		this.videoid = videoid;
	}

	public Integer getSegmentNo() {
		return segmentNo;
	}

	public void setSegmentNo(Integer segmentNo) {
		this.segmentNo = segmentNo;
	}

	public Integer getStartTime() {
		return startTime;
	}

	public void setStartTime(Integer startTime) {
		this.startTime = startTime;
	}

	public Integer getEndTime() {
		return endTime;
	}

	public void setEndTime(Integer endTime) {
		this.endTime = endTime;
	}

	public Integer getDuration() {
		return this.endTime - this.startTime;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getTransitionType() {
		return transitionType;
	}

	public void setTransitionType(String transitionType) {
		this.transitionType = transitionType;
	}

	public Integer getTransitionDuration() {
		return transitionDuration;
	}

	public void setTransitionDuration(Integer transitionDuration) {
		this.transitionDuration = transitionDuration;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getVideoname() {
		return videoname;
	}

	public void setVideoname(String videoname) {
		this.videoname = videoname;
	}

	@Override
	public String toString() {
		return "VideoMixSegmentEntity [id=" + id + ", videomixid=" + videomixid + ", videoid=" + videoid
				+ ", videoname=" + videoname + ", segmentNo=" + segmentNo + ", startTime=" + startTime + ", endTime="
				+ endTime + ", duration=" + duration + ", transitionType=" + transitionType + ", transitionDuration="
				+ transitionDuration + ", createTime=" + createTime + "]";
	}

}
