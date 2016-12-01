package com.pivotal.cf.broker.model;

import java.io.Serializable;

public class DockerMountEntry implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = 1L;

	private String container_path;
	
	private String mount_path;
	
	private String mount_size;

	public String getContainer_path() {
		return container_path;
	}

	public void setContainer_path(String container_path) {
		this.container_path = container_path;
	}

	public String getMount_path() {
		return mount_path;
	}

	public void setMount_path(String mount_path) {
		this.mount_path = mount_path;
	}

	public String getMount_size() {
		return mount_size;
	}

	public void setMount_size(String mount_size) {
		this.mount_size = mount_size;
	}
	
	
}
