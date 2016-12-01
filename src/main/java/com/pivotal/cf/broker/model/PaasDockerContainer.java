/**   
* @Title: PaasDockerContainers.java 
* @Package com.mopaas.paas.docker.entry 
* @Description: TODO(用一句话描述该文件做什么) 
* @author milliant xfzhou@anchora.me   
* @date 2015年1月26日 下午2:19:13 
* @version V1.0   
*/
package com.pivotal.cf.broker.model;

import java.io.Serializable;

/** 
 * @ClassName: PaasDockerContainers 
 * @Description: 封装docker 容器的相关信息
 * @author milliant xfzhou@anchora.me 
 * @date 2015年1月26日 下午2:19:13 
 *  
 */

public class PaasDockerContainer implements Serializable{

	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String image;
	private String engine;
	private String[]hosts;
	//分页数据　总数据数
	private String totalcount;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}

	public String getEngine() {
		return engine;
	}
	public void setEngine(String engine) {
		this.engine = engine;
	}
	public String getTotalcount() {
		return totalcount;
	}
	public void setTotalcount(String totalcount) {
		this.totalcount = totalcount;
	}
	public String[] getHosts() {
		return hosts;
	}
	public void setHosts(String[] hosts) {
		this.hosts = hosts;
	}
	
}
