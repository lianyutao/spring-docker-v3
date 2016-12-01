package com.pivotal.cf.broker.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The response from the broker sent back to the cloud controller 
 * on a successful service instance creation request
 * 
 * @author sgreenberg@gopivotal.com
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class UpdateServiceInstanceResponse {

	private String returnCode;
	
	public UpdateServiceInstanceResponse() {}
	
	public UpdateServiceInstanceResponse(String returnCode) {
		this.returnCode = returnCode;
	}

	@JsonSerialize
	@JsonProperty("returnCode")
	public String getReturnCode() {
		return returnCode;
	}
	
}
