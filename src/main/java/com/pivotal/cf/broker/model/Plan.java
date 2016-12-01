package com.pivotal.cf.broker.model;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * A service plan available for a ServiceDefinition
 * 
 * @author sgreenberg@gopivotal.com
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class Plan {

	@NotEmpty
	@JsonSerialize
	@JsonProperty("id")
	private String id;
	
	@NotEmpty
	@JsonSerialize
	@JsonProperty("name")
	private String name;
	
	@NotEmpty
	@JsonSerialize
	@JsonProperty("description")
	private String description;
	
	
	@JsonSerialize
	@JsonProperty("free")
	private Boolean free;
	
	@JsonSerialize
	@JsonProperty("metadata")
	private Map<String,Object> metadata = new HashMap<String,Object>();
	
	public Plan(String id, String name, String description,Boolean free) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.free = free;
	}

	public Plan(String id, String name, String description,Boolean free, Map<String,Object> metadata) {
		this(id, name, description,free);
		setMetadata(metadata);
	}
	
	public Plan(String id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Boolean getFree() {
		return free;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}
	
	private void setMetadata(Map<String, Object> metadata) {
		if (metadata == null) {
			this.metadata = new HashMap<String,Object>();
		} else {
			this.metadata = metadata;
		}
	}
	
}
