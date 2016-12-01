package com.pivotal.cf.broker.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * An instance of a ServiceDefinition.
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class ServiceInstance {

	@JsonSerialize
	@JsonProperty("service_instance_id")
	private String id;
	
	@JsonSerialize
	@JsonProperty("service_id")
	private String serviceDefinitionId;
	
	@JsonSerialize
	@JsonProperty("plan_id")
	private String planId;
	
	@JsonSerialize
	@JsonProperty("organization_guid")
	private String organizationGuid;
	
	@JsonSerialize
	@JsonProperty("space_guid")
	private String spaceGuid;
	
	@JsonSerialize
	@JsonProperty("dashboard_url")
	private String dashboardUrl;
	
	@JsonSerialize
	@JsonProperty("containerId")
	private String containerId;
	
	@JsonSerialize
	@JsonProperty("containerHost")
	private String containerHost;
	
	@JsonSerialize
	@JsonProperty("containerPort")
	private String containerPort;
	
	@JsonSerialize
	@JsonProperty("containerUser")
	private String containerUser;

	@JsonSerialize
	@JsonProperty("containerPasswd")
	private String containerPasswd;
	
	@JsonSerialize
	@JsonProperty("containerDb")
	private String containerDb;
	

	@SuppressWarnings("unused")
	private ServiceInstance() {}
	
	public ServiceInstance( String id, String serviceDefinitionId, String planId, String organizationGuid, String spaceGuid, String dashboardUrl, 
			String containerId, String containerHost, String containerPort, String containerUser, String containerPasswd, String containerDb) {
		setId(id);
		setServiceDefinitionId(serviceDefinitionId);
		setPlanId(planId);
		setOrganizationGuid(organizationGuid);
		setSpaceGuid(spaceGuid);
		setDashboardUrl(dashboardUrl);
		setContainerId(containerId);
		setContainerHost(containerHost);
		setContainerPort(containerPort);
		setContainerUser(containerUser);
		setContainerPasswd(containerPasswd);
		setContainerDb(containerDb);
	}
	
	public ServiceInstance( String id, String serviceDefinitionId, String planId, String organizationGuid, String spaceGuid, String dashboardUrl, 
			String containerId, String containerHost, String containerPort, String containerPasswd) {
		setId(id);
		setServiceDefinitionId(serviceDefinitionId);
		setPlanId(planId);
		setOrganizationGuid(organizationGuid);
		setSpaceGuid(spaceGuid);
		setDashboardUrl(dashboardUrl);
		setContainerId(containerId);
		setContainerHost(containerHost);
		setContainerPort(containerPort);
		setContainerPasswd(containerPasswd);
	}
	
	public ServiceInstance( String id, String serviceDefinitionId, String planId, String organizationGuid, String spaceGuid, String dashboardUrl, 
			String containerId, String containerHost, String containerPort, String containerUser, String containerPasswd) {
		setId(id);
		setServiceDefinitionId(serviceDefinitionId);
		setPlanId(planId);
		setOrganizationGuid(organizationGuid);
		setSpaceGuid(spaceGuid);
		setDashboardUrl(dashboardUrl);
		setContainerId(containerId);
		setContainerHost(containerHost);
		setContainerPort(containerPort);
		setContainerUser(containerUser);
		setContainerPasswd(containerPasswd);
	}
	
	
	public String getId() {
		return id;
	}

	private void setId(String id) {
		this.id = id;
	}

	public String getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	private void setServiceDefinitionId(String serviceDefinitionId) {
		this.serviceDefinitionId = serviceDefinitionId;
	}

	public String getPlanId() {
		return planId;
	}

	private void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getOrganizationGuid() {
		return organizationGuid;
	}

	private void setOrganizationGuid(String organizationGuid) {
		this.organizationGuid = organizationGuid;
	}

	public String getSpaceGuid() {
		return spaceGuid;
	}

	private void setSpaceGuid(String spaceGuid) {
		this.spaceGuid = spaceGuid;
	}

	public String getDashboardUrl() {
		return dashboardUrl;
	}

	private void setDashboardUrl(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
	}
	

	public String getContainerId() {
		return containerId;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	public String getContainerHost() {
		return containerHost;
	}

	public void setContainerHost(String containerHost) {
		this.containerHost = containerHost;
	}

	public String getContainerPort() {
		return containerPort;
	}

	public void setContainerPort(String containerPort) {
		this.containerPort = containerPort;
	}

	public String getContainerUser() {
		return containerUser;
	}

	public void setContainerUser(String containerUser) {
		this.containerUser = containerUser;
	}

	public String getContainerPasswd() {
		return containerPasswd;
	}

	public void setContainerPasswd(String containerPasswd) {
		this.containerPasswd = containerPasswd;
	}


	public String getContainerDb() {
		return containerDb;
	}

	public void setContainerDb(String containerDb) {
		this.containerDb = containerDb;
	}
}
