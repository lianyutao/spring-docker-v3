package com.pivotal.cf.broker.model;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Request sent from the cloud controller to bind to a service 
 * instance.
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class ServiceInstanceBindingRequest {

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
	@JsonProperty("app_guid")
	private String appGuid;
	@JsonSerialize
	@JsonProperty("bind_resource")
	private Map<Object,Object> bindResource;
	@JsonSerialize
	@JsonProperty("parameters")
	private Map<Object,Object> parameters;
	
	public ServiceInstanceBindingRequest() {}
	
	public ServiceInstanceBindingRequest(String serviceDefinitionId, String planId, String appGuid,Map<Object,Object> bindResource,
			Map<Object,Object> parameters) {
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.appGuid = appGuid;
		this.bindResource = bindResource;
		this.parameters = parameters;
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

	public String getAppGuid() {
		return appGuid;
	}

	public void setAppGuid(String appGuid) {
		this.appGuid = appGuid;
	}

	public Map<Object, Object> getBindResource() {
		return bindResource;
	}

	public void setBindResource(Map<Object, Object> bindResource) {
		this.bindResource = bindResource;
	}

	public Map<Object, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<Object, Object> parameters) {
		this.parameters = parameters;
	}
	
}
