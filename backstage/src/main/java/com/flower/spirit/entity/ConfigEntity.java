package com.flower.spirit.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * 基础配置 app 配置
 * @author flower
 *
 */
@Entity
@Table(name = "biz_config")
public class ConfigEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3373882641617252642L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="biz_config_seq")
	@TableGenerator(name = "biz_config_seq", allocationSize = 1, table = "seq_common", pkColumnName = "seq_id", valueColumnName = "seq_count")
    private Integer id;
	
	private String apptoken;
	
	private String ipauth;
	
	private String openprocesshistory;
	
	private String taskcron; //任务定时器
	
	private String generatenfo;
	
	private String agenttype; //类型
	
	private String agentaddress;  //地址
	
	private String agentport;  //端口
	
	private String agentaccpass;  //密码

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getApptoken() {
		return apptoken;
	}

	public void setApptoken(String apptoken) {
		this.apptoken = apptoken;
	}

	public String getIpauth() {
		return ipauth;
	}

	public void setIpauth(String ipauth) {
		this.ipauth = ipauth;
	}

	public String getOpenprocesshistory() {
		return openprocesshistory;
	}

	public void setOpenprocesshistory(String openprocesshistory) {
		this.openprocesshistory = openprocesshistory;
	}

	public String getTaskcron() {
		return taskcron;
	}

	public void setTaskcron(String taskcron) {
		this.taskcron = taskcron;
	}

	public String getGeneratenfo() {
		return generatenfo;
	}

	public void setGeneratenfo(String generatenfo) {
		this.generatenfo = generatenfo;
	}

	public String getAgenttype() {
		return agenttype;
	}

	public void setAgenttype(String agenttype) {
		this.agenttype = agenttype;
	}

	public String getAgentaddress() {
		return agentaddress;
	}

	public void setAgentaddress(String agentaddress) {
		this.agentaddress = agentaddress;
	}

	public String getAgentport() {
		return agentport;
	}

	public void setAgentport(String agentport) {
		this.agentport = agentport;
	}

	public String getAgentaccpass() {
		return agentaccpass;
	}

	public void setAgentaccpass(String agentaccpass) {
		this.agentaccpass = agentaccpass;
	}

	
	
	

}
