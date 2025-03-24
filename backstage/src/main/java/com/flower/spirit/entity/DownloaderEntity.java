package com.flower.spirit.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * 下载器配置
 * @author flower
 *
 */
@Entity
@Table(name = "biz_downloader")
public class DownloaderEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 441169571615432179L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="downloader_seq")
	@TableGenerator(name = "downloader_seq", allocationSize = 1, table = "seq_common", pkColumnName = "seq_id", valueColumnName = "seq_count")
    private Integer id;
	
	private String downloadername;
	
	private String downloadertype;
	
	private String downloaderlink;
	
	private String downloaderport;
	
	private String downloadpath;
	
	private String username;
	
	private String password;
	
	private String token;
	
	private String status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDownloadername() {
		return downloadername;
	}

	public void setDownloadername(String downloadername) {
		this.downloadername = downloadername;
	}

	public String getDownloadertype() {
		return downloadertype;
	}

	public void setDownloadertype(String downloadertype) {
		this.downloadertype = downloadertype;
	}

	public String getDownloaderlink() {
		return downloaderlink;
	}

	public void setDownloaderlink(String downloaderlink) {
		this.downloaderlink = downloaderlink;
	}

	public String getDownloaderport() {
		return downloaderport;
	}

	public void setDownloaderport(String downloaderport) {
		this.downloaderport = downloaderport;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDownloadpath() {
		return downloadpath;
	}

	public void setDownloadpath(String downloadpath) {
		this.downloadpath = downloadpath;
	}
	
	

}
