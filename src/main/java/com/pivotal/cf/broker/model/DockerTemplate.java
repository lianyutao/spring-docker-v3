package com.pivotal.cf.broker.model;

import java.io.Serializable;
import java.util.List;

import net.sf.json.JSONObject;

public class DockerTemplate implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = 1L;

	private String image="";
	
	private String host="";
	
	private String domain="";
	
	private String container="";
	
	private List<DockerBaseEntry> env;
	
	private List<DockerBaseEntry> arg;
	
	private List<DockerMountEntry> mount;
	
	private List<DockerBaseEntry> links;
	
	private Double cpu;
	
	private Integer memory;
	
	private Integer instances;
	
	private String type;
	
	private String restartpolicy;
	
	private String network;
	
	private Integer mountSize;
	private Integer disk;
//	private JSONObject planInfo;
	private List<DockerPortTemplate> port;

	private String privileged;
	private String tty;
	public Integer getMountSize() {
		return mountSize;
	}

	public void setMountSize(Integer mountSize) {
		this.mountSize = mountSize;
	}
	

	public String getImage() {
		return image;
	}

//	public JSONObject getPlanInfo() {
//		return planInfo;
//	}
//
//	public void setPlanInfo(JSONObject planInfo) {
//		this.planInfo = planInfo;
//	}

	public Integer getDisk() {
		return disk;
	}

	public void setDisk(Integer disk) {
		this.disk = disk;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	public List<DockerBaseEntry> getEnv() {
		return env;
	}

	public void setEnv(List<DockerBaseEntry> env) {
		this.env = env;
	}

	public List<DockerBaseEntry> getArg() {
		return arg;
	}

	public void setArg(List<DockerBaseEntry> arg) {
		this.arg = arg;
	}

	public List<DockerMountEntry> getMount() {
		return mount;
	}

	public void setMount(List<DockerMountEntry> mount) {
		this.mount = mount;
	}

	public List<DockerBaseEntry> getLinks() {
		return links;
	}

	public void setLinks(List<DockerBaseEntry> links) {
		this.links = links;
	}

	public Double getCpu() {
		return cpu;
	}

	public void setCpu(Double cpu) {
		this.cpu = cpu;
	}

	public Integer getMemory() {
		return memory;
	}

	public void setMemory(Integer memory) {
		this.memory = memory;
	}

	public Integer getInstances() {
		return instances;
	}

	public void setInstances(Integer instances) {
		this.instances = instances;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRestartpolicy() {
		return restartpolicy;
	}

	public void setRestartpolicy(String restartpolicy) {
		this.restartpolicy = restartpolicy;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public List<DockerPortTemplate> getPort() {
		return port;
	}

	public void setPort(List<DockerPortTemplate> port) {
		this.port = port;
	}

	public String getPrivileged() {
		return privileged;
	}

	public void setPrivileged(String privileged) {
		this.privileged = privileged;
	}

	public String getTty() {
		return tty;
	}

	public void setTty(String tty) {
		this.tty = tty;
	}

	
}
