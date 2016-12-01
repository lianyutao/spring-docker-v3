package com.pivotal.cf.broker.model;

import java.io.Serializable;

public class DockerPortTemplate implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = 1L;

	private String protocol="";
	
	private String ip="";
	
	private Integer port;
	
	private Integer containerport;

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Integer getContainerport() {
		return containerport;
	}

	public void setContainerport(Integer containerport) {
		this.containerport = containerport;
	}

	
}
