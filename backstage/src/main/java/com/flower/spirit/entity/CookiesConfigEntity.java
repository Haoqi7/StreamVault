package com.flower.spirit.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "biz_cookies_config")
public class CookiesConfigEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 574236590065434047L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="biz_cookies_config")
	@TableGenerator(name = "biz_cookies_config", allocationSize = 1, table = "seq_common", pkColumnName = "seq_id", valueColumnName = "seq_count")
    private Integer id;
	
	private String youtubecookies;
	
	private String twittercookies;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getYoutubecookies() {
		return youtubecookies;
	}

	public void setYoutubecookies(String youtubecookies) {
		this.youtubecookies = youtubecookies;
	}

	public String getTwittercookies() {
		return twittercookies;
	}

	public void setTwittercookies(String twittercookies) {
		this.twittercookies = twittercookies;
	}

	
	
	
	

}
