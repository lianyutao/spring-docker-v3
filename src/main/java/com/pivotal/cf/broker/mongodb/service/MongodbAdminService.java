package com.pivotal.cf.broker.mongodb.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.pivotal.cf.broker.exception.ServiceInstanceCreateException;
import com.pivotal.cf.broker.model.DockerBaseEntry;
import com.pivotal.cf.broker.model.DockerMountEntry;
import com.pivotal.cf.broker.model.DockerPortTemplate;
import com.pivotal.cf.broker.model.DockerTemplate;
import com.pivotal.cf.broker.util.JsonUtil;
import com.pivotal.cf.broker.util.PropertyUtil;

/**
 * Utility class for manipulating a Mongo database.
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
@Service
public class MongodbAdminService {
	
	private Logger logger = LoggerFactory.getLogger(MongodbAdminService.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
//	
//	SimpleClientHttpRequestFactory requestFactory;
//	
//	public MysqlAdminService(SimpleClientHttpRequestFactory requestFactory){
//		this.requestFactory = requestFactory;
//	}
	String docker = PropertyUtil.getProperty("docker");
	
	private final String createContainerurl = "http://" + docker + "/docker/containers/create.html";
	private final String stopContainerurl = "http://" + docker + "/docker/containers/stop.html";
	private final String deleteContainerurl = "http://" + docker + "/docker/containers/delete.html";
	private final String updateContainerurl = "http://" + docker + "/docker/containers/upgrade.html";

//	private static final String createContainerurl = "http://192.168.7.250:8088/paas_docker/docker/containers/create.html";
//	private static final String stopContainerurl = "http://192.168.7.250:8088/paas_docker/docker/containers/stop.html";
//	private static final String deleteContainerurl = "http://192.168.7.250:8088/paas_docker/docker/containers/delete.html";
	@SuppressWarnings("unchecked")
	public Map<String, Object> createContainer(RestTemplate restTemplate, JSONObject plan) {
		//MultiValueMap<String, String> containerParamsMap = new LinkedMultiValueMap<String, String>();
		//containerParamsMap.add("image", "tutum/mysql");
		//String createParams = "{\"image\":\"tutum/mysql\"}";
//		String createParams = "{\"image\":\"mongodb-2.6.3\"}";
		DockerTemplate temp=new DockerTemplate();
		temp.setImage("mongo");
		//temp.setMemory(memory);
//		temp.setMountSize(mountSize);
//		temp.setPlanInfo(mountSize);
		if(plan.get("memoryVal")!=null){
			temp.setMemory(Integer.parseInt((String) plan.get("memoryVal")));
		}
		if(plan.get("cpu")!=null){
			temp.setCpu(Double.parseDouble((String) plan.get("cpu")));
		}
		if(plan.get("diskVal")!=null){
			temp.setMountSize(Integer.parseInt((String) plan.get("diskVal")));
		}
		String createResult = restTemplate.postForObject(createContainerurl, temp, String.class);
		logger.info("createResult: " + createResult);
		
		if(!"000".equals(JSONObject.fromObject(createResult).getString("returnCode"))){
			try{
				throw new ServiceInstanceCreateException();
			} catch(ServiceInstanceCreateException e){
				logger.error("create container failed! error NO. is: "+ JSONObject.fromObject(createResult).getString("returnCode"));
			}
		}
		//Map<String, String> resultMap = (Map<String, String>) tmpMap.get("result");
		
		Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
        classMap.put("port", DockerPortTemplate.class);
        classMap.put("env", DockerBaseEntry.class);
        classMap.put("arg", DockerBaseEntry.class);
        classMap.put("mount", DockerMountEntry.class);
        classMap.put("links", DockerBaseEntry.class);
//        json = (DockerTemplate) JsonUtil.toObject(obj.toString(), DockerTemplate.class, classMap);
		
		DockerTemplate dockerTemplate = (DockerTemplate) JsonUtil.toObject(JSONObject.fromObject(createResult).getString("result"), DockerTemplate.class, classMap);
			//String str = HttpClientUtils.postByHttp("http://localhost:9094/password.html", "{\"userCode\":\"f2124a7d-961b-4ad8-9f98-6302ee0588b1\",\"userPwd\":\"654321\"}");
		
		Map<String, Object> showResult = new HashMap<String, Object>();
		List<DockerBaseEntry> envList = dockerTemplate.getEnv();
		List<DockerPortTemplate> portList = dockerTemplate.getPort();
		showResult.put("containerId", dockerTemplate.getContainer());
		showResult.put("containerIp", portList.get(0).getIp());
		showResult.put("containerPort",portList.get(0).getContainerport());
		showResult.put("containerUser", getDockerBaseEntryValue(envList, "COMMON_USER"));
		showResult.put("containerPasswd", getDockerBaseEntryValue(envList, "COMMON_PASS"));
		showResult.put("containerDb", getDockerBaseEntryValue(envList, "COMMON_DB"));		
		
		return showResult;			
	}
	
	
	public String getDockerBaseEntryValue(List<DockerBaseEntry> listToDeal, String key){
		String value = "";
		for(int i = 0; i< listToDeal.size(); i++){
			DockerBaseEntry dockerBaseEntry  = (DockerBaseEntry) listToDeal.get(i);
			if(key.equals(dockerBaseEntry.getKey())){
				value = (String) dockerBaseEntry.getValue();
				break;
			}
		}
		return value;
	}
	
//	public String getDockerPortTemplateValue(List<DockerPortTemplate> listToDeal, String key){
//		String value = "";
//		for(int i = 0; i< listToDeal.size(); i++){
//			DockerPortTemplate dockerPortTemplate  = (DockerPortTemplate) listToDeal.get(i);
//			if(key.equals(dockerPortTemplate.getKey())){
//				value = (String) dockerPortTemplate.getValue();
//				break;
//			}
//		}
//		return value;
//	}
	
	public boolean stopContainer(RestTemplate restTemplate, String containerId, String containerHost) {
		MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
		request.add("ids[]", containerId);
		request.add("hosts[]", containerHost);
		//String stopParams = "{\"ids\":\"" + containerId + "\", \"hosts\":\"" + containerHost + "\"}";
		try {
			String stopResult = restTemplate.postForObject(stopContainerurl, request, String.class);
			logger.info("stopResult: " + stopResult);
			String returnCode = (String) JsonUtil.getMapFromJson(stopResult).get("returnCode");
			if(!"000".equals(returnCode)){
				logger.error("stop container failed! error NO. is: " + returnCode);
				return false;
			}
		}catch (Exception e){
			logger.error(e.getMessage());
		}				
		return true;
	}
	
	public boolean deleteContainer(RestTemplate restTemplate, String containerId, String containerHost) {
		MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
		request.add("ids[]", containerId);
		request.add("hosts[]", containerHost);
		//String deleteParams = "{\"ids\":\"" + containerId + "\", \"hosts\":\"" + containerHost + "\"}";
		try {
			String deleteResult = restTemplate.postForObject(deleteContainerurl, request, String.class);
			logger.info("deleteResult: " + deleteResult);
			String returnCode = (String) JsonUtil.getMapFromJson(deleteResult).get("returnCode");
			if(!"000".equals(returnCode)){
				logger.error("delete container failed! error NO. is: " + returnCode);
				return false;
			}
		}catch (Exception e){
			logger.error(e.getMessage());
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean updateContainer(RestTemplate restTemplate, String containerId, String host, JSONObject plan) {
		Map<String, String> map=new HashMap<String, String>();
		map.put("id", containerId);
		map.put("host", host);
//		map.put("size", String.valueOf(mountSize));
		if(plan.get("memoryVal")!=null && !plan.get("memoryVal").equals("0")){
			map.put("memory", (String) plan.get("memoryVal"));
		}
		if(plan.get("diskVal")!=null && !plan.get("diskVal").equals("0")){
			map.put("size", (String) plan.get("diskVal"));
		}
		if(plan.get("cpu")!=null){
			map.put("cpu", (String) plan.get("cpu"));
		}
		
		
		String updateResult = restTemplate.postForObject(updateContainerurl, JsonUtil.toJson(map), String.class);
		logger.info("updateResult: " + updateResult);

		if(!"000".equals(JSONObject.fromObject(updateResult).getString("returnCode"))){
			try{
				throw new ServiceInstanceCreateException();
			} catch(ServiceInstanceCreateException e){
				logger.error("update container failed! error NO. is: "+ JSONObject.fromObject(updateResult).getString("returnCode"));
			}
			return false;
		}
		return true;		
	}
	public static void main(String[] args) {
		JSONObject obj = new JSONObject();
		obj.accumulate("double", 2.01);
		obj.accumulate("string", "string str");
		obj.accumulate("key", "100M");
		System.out.println(obj);
		System.out.println(obj.getDouble("double"));
		System.out.println(obj.getString("string"));
	}
}
