package com.pivotal.cf.broker.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class Previous {
	@NotEmpty
	@JsonSerialize
	@JsonProperty("plan_id")
	private String planId;
	@NotEmpty
	@JsonSerialize
	@JsonProperty("service_id")
	private String instanceId;
	
	@JsonProperty("organization_id")
	private String organizationId;
	
	@NotEmpty
	@JsonSerialize
	@JsonProperty("space_id")
	private String spaceId;
	

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(String spaceId) {
		this.spaceId = spaceId;
	}
	

}
