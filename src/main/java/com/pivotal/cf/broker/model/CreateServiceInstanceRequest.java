package com.pivotal.cf.broker.model;

import net.sf.json.JSONObject;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * A request sent by the cloud controller to create a new instance
 * of a service.
 * 
 * @author sgreenberg@gopivotal.com
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class CreateServiceInstanceRequest {

	@NotEmpty
	@JsonSerialize
	@JsonProperty("service_id")
	private String serviceDefinitionId;
	
	@NotEmpty
	@JsonSerialize
	@JsonProperty("plan_id")
	private String planId;
	
	@NotEmpty
	@JsonSerialize
	@JsonProperty("organization_guid")
	private String organizationGuid;
	
	@NotEmpty
	@JsonSerialize
	@JsonProperty("space_guid")
	private String spaceGuid;
	
	@JsonSerialize
	@JsonProperty("parameters")
	private JSONObject parameters;
	
//	@NotEmpty
//	@JsonSerialize
//	@JsonProperty("space_id")
//	private String spaceId;


	public CreateServiceInstanceRequest() {}
	
	public CreateServiceInstanceRequest(String serviceDefinitionId, String planId, String organizationGuid, String spaceGuid) {
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.organizationGuid = organizationGuid;
		this.spaceGuid = spaceGuid;
//		this.spaceId = spaceId;
	}
	
//	public CreateServiceInstanceRequest(String serviceDefinitionId, String planId, String organizationGuid, String spaceGuid) {
//		this.serviceDefinitionId = serviceDefinitionId;
//		this.planId = planId;
//		this.organizationGuid = organizationGuid;
//		this.spaceGuid = spaceGuid;
//	}
	
	public String getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	public void setServiceDefinitionId(String serviceDefinitionId) {
		this.serviceDefinitionId = serviceDefinitionId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getOrganizationGuid() {
		return organizationGuid;
	}

	public void setOrganizationGuid(String organizationGuid) {
		this.organizationGuid = organizationGuid;
	}

	public String getSpaceGuid() {
		return spaceGuid;
	}

	public void setSpaceGuid(String spaceGuid) {
		this.spaceGuid = spaceGuid;
	}

	public JSONObject getParameters() {
		return parameters;
	}

	public void setParameters(JSONObject parameters) {
		this.parameters = parameters;
	}
	
	
	
	
//	public String getSpaceId() {
//		return spaceId;
//	}
//
//	public void setSpaceId(String spaceId) {
//		this.spaceId = spaceId;
//	}

}
