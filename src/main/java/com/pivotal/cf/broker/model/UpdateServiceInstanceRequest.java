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
public class UpdateServiceInstanceRequest {

	@NotEmpty
	@JsonSerialize
	@JsonProperty("service_id")
	private String serviceDefinitionId;
	
	@NotEmpty
	@JsonSerialize
	@JsonProperty("plan_id")
	private String planId;

	@JsonSerialize
	@JsonProperty("previous_values")
	private Previous previous;
	
	@JsonSerialize
	@JsonProperty("parameters")
	private JSONObject parameters;
	
	
	


	public UpdateServiceInstanceRequest() {}
	
	public UpdateServiceInstanceRequest(String serviceDefinitionId, String planId) {
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
//		this.serviceInstanceId = serviceInstanceId;
	}
	


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

	public Previous getPrevious() {
		return previous;
	}

	public void setPrevious(Previous previous) {
		this.previous = previous;
	}

	public JSONObject getParameters() {
		return parameters;
	}

	public void setParameters(JSONObject parameters) {
		this.parameters = parameters;
	}


	
}
