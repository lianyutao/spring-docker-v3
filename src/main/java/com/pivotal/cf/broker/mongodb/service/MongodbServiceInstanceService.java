package com.pivotal.cf.broker.mongodb.service;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pivotal.cf.broker.exception.ServiceBrokerException;
import com.pivotal.cf.broker.exception.ServiceInstanceDoesNotExistException;
import com.pivotal.cf.broker.exception.ServiceInstanceExistsException;
import com.pivotal.cf.broker.exception.ServiceInstanceIsBoundException;
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.repo.MongodbPersistedServiceRepo;
import com.pivotal.cf.broker.service.ServiceInstanceService;
import com.pivotal.cf.broker.util.RestTemplateUtil;

/**
 * Mysql impl to mysql service instances.  Creating a service does the following:
 * creates a new database,
 * saves the ServiceInstance info to the Mysql repository.
 *  
 * @author sgreenberg@gopivotal.com
 *
 */
@Service
public class MongodbServiceInstanceService implements ServiceInstanceService {
//	public static Map<String, ServiceInstance> serviceInstances = new HashMap<String, ServiceInstance>();
	private Logger logger = LoggerFactory.getLogger(MongodbServiceInstanceService.class);
	private MongodbAdminService mongodb;
	@Autowired
	private MongodbPersistedServiceRepo mongodbServiceRepo;
	
	@Autowired
	public MongodbServiceInstanceService(MongodbAdminService mongodb) {
		this.mongodb = mongodb;
	}
	
	@Override
	public List<ServiceInstance> getAllServiceInstances() {
		return mongodbServiceRepo.getInstances();
	}

	@Override
	public ServiceInstance createServiceInstance(ServiceDefinition service,
			String serviceInstanceId, String planId, String organizationGuid,
			String spaceGuid) 
			throws ServiceInstanceExistsException, ServiceBrokerException {
		ServiceInstance instance = getServiceInstance(serviceInstanceId);
		if (instance != null) {
			throw new ServiceInstanceExistsException(instance);
		}
//		int mountSize;
//		if("Default Mongodb-docker Plan".equals(planId)){
//			mountSize = 10;
//		}else{
//			String planNum = planId.substring(planId.lastIndexOf("-")+1, planId.length()-1);
//			if(planNum.startsWith("0.")){
//				mountSize = (int) (Float.parseFloat(planNum) * 1024);
//			} else{
//				mountSize = Integer.parseInt(planNum) * 1024;	
//			}				
//		}
		int mountSize = -1;
		JSONObject plan = JSONObject.fromObject(mongodbServiceRepo.getPlanExtra(planId));
		logger.info("createServiceInstance plan extra "+ plan);
//		if( plan.get("diskVal")!=null&&!plan.get("diskVal").equals("")){
//			System.out.println("plan");
//			mountSize = Integer.getInteger((String) plan.get("diskVal"));
//		}
//		if(null!=plan.get("memory")){
//			String memory = (String) plan.get("memory");
//		}
//		if(null!=plan.get("cpu")){
//			String cpu = (String) plan.get("cpu");
//		}
//		if(null!=plan.get("disk")){
//			String disk = (String) plan.get("disk");
//		}

		Map<String, Object> containerInfoMap = mongodb.createContainer(RestTemplateUtil.getRestTemplate(), plan);
		
		if(containerInfoMap != null){
			instance = new ServiceInstance(
					serviceInstanceId, 
					service.getId(),
					planId,
					organizationGuid,
					spaceGuid,
					"http://phpadmin.tiger.mopaas.com",  //dashboard url is not implemented yet
					containerInfoMap.get("containerId").toString(),
					containerInfoMap.get("containerIp").toString(),
					containerInfoMap.get("containerPort").toString(),
					containerInfoMap.get("containerUser").toString(),
					containerInfoMap.get("containerPasswd").toString(),
					containerInfoMap.get("containerDb").toString()
					);
			mongodbServiceRepo.addInstance(serviceInstanceId, instance);			
		}
		return instance;
	}

	@Override
	public ServiceInstance getServiceInstance(String id) {
		return mongodbServiceRepo.getInstance(id);
	}

	@Override
	public ServiceInstance deleteServiceInstance(String id) {
		ServiceInstance instance = getServiceInstance(id);
		if(instance != null && !mongodbServiceRepo.isInstanceBound(id)) {
//			if(mongodb.stopContainer(RestTemplateUtil.getRestTemplate(), instance.getContainerId(), instance.getContainerHost())){
				mongodb.deleteContainer(RestTemplateUtil.getRestTemplate(), instance.getContainerId(), instance.getContainerHost());
				mongodbServiceRepo.removeInstance(id);
				return instance;
			}
//		}
		
//		if(mongodbServiceRepo.isInstanceBound(id)) {
//			try {
//				throw new ServiceInstanceIsBoundException(instance);
//			} catch (ServiceInstanceIsBoundException e) {
//				e.printStackTrace();
//			}
//		}
		
	
		return null;		
	}
	
	public ServiceInstance updateServiceInstance(ServiceDefinition service,
			String serviceInstanceId, String planId, String organizationGuid,
			String spaceGuid) 
			throws ServiceInstanceExistsException, ServiceBrokerException, ServiceInstanceDoesNotExistException {
		ServiceInstance instance = getServiceInstance(serviceInstanceId);
		if (instance == null) {
			throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
		}
//		int mountSize;
//		if("Default Mongodb-docker Plan".equals(planId)){
//			mountSize = 10;
//		}else{
//			String planNum = planId.substring(planId.lastIndexOf("-")+1, planId.length()-1);
//			if(planNum.startsWith("0.")){
//				mountSize = (int) (Float.parseFloat(planNum) * 1024);
//			} else{
//				mountSize = Integer.parseInt(planNum) * 1024;	
//			}				
//		}
		JSONObject planExtra = JSONObject.fromObject(mongodbServiceRepo.getPlanExtra(planId));
		if(mongodb.updateContainer(RestTemplateUtil.getRestTemplate(), instance.getContainerId(), instance.getContainerHost(),  planExtra)){
			instance = new ServiceInstance(
					serviceInstanceId, 
					service.getId(),
					planId,
					organizationGuid,
					spaceGuid,
					"http://phpadmin.tiger.mopaas.com",  //dashboard url is not implemented yet
					instance.getContainerId(),
					instance.getContainerHost(),
					instance.getContainerPort(),
					instance.getContainerUser(),
					instance.getContainerPasswd(),
					instance.getContainerDb()
					);
			mongodbServiceRepo.updateInstance(serviceInstanceId, instance);
		}
		return instance;
	}
}