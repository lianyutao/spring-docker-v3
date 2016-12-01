package com.pivotal.cf.broker.redis.service;

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
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.repo.RedisPersistedServiceRepo;
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
public class RedisServiceInstanceService implements ServiceInstanceService {
//	public static Map<String, ServiceInstance> serviceInstances = new HashMap<String, ServiceInstance>();
	private Logger logger = LoggerFactory.getLogger(RedisServiceInstanceService.class);
	private RedisAdminService redis;
	@Autowired
	private RedisPersistedServiceRepo redisServiceRepo;
	
	@Autowired
	public RedisServiceInstanceService(RedisAdminService redis) {
		this.redis = redis;
	}
	
	@Override
	public List<ServiceInstance> getAllServiceInstances() {
		return redisServiceRepo.getInstances();
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
		int memory = -1;
//		if("redis-docker-plan".equals(planId)){
//			memory = 128;
//		}else{
//			memory = Integer.parseInt(planId.substring(planId.lastIndexOf("-")+1, planId.length()-1));
//		}
		JSONObject plan = JSONObject.fromObject(redisServiceRepo.getPlanExtra(planId));
		logger.info("createServiceInstance plan extra "+ plan);
//		if(plan.get("memory")!=null){
//			memory = Integer.parseInt((String) plan.get("memory"));
//		}
		Map<String, Object> containerInfoMap = redis.createContainer(RestTemplateUtil.getRestTemplate(), plan);
		
		if(containerInfoMap != null){
			instance = new ServiceInstance(
					serviceInstanceId, 
					service.getId(),
					planId,
					organizationGuid,
					spaceGuid,
					"http://redisadmin.tiger.mopaas.com",  //dashboard url is not implemented yet
					containerInfoMap.get("containerId").toString(),
					containerInfoMap.get("containerIp").toString(),
					containerInfoMap.get("containerPort").toString(),
					containerInfoMap.get("containerPasswd").toString()
					);
			redisServiceRepo.addInstance(serviceInstanceId, instance);			
		}
		return instance;
	}

	@Override
	public ServiceInstance getServiceInstance(String id) {
		return redisServiceRepo.getInstance(id);
	}

	@Override
	public ServiceInstance deleteServiceInstance(String id) {
		ServiceInstance instance = getServiceInstance(id);
		if(instance != null && !redisServiceRepo.isInstanceBound(id)) {
//			if(redis.stopContainer(RestTemplateUtil.getRestTemplate(), instance.getContainerId(), instance.getContainerHost())){
				redis.deleteContainer(RestTemplateUtil.getRestTemplate(), instance.getContainerId(), instance.getContainerHost());
				redisServiceRepo.removeInstance(id);		
				return instance;
			}
//		}
		
//		if(redisServiceRepo.isInstanceBound(id)) {
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
//		if (instance == null) {
//			throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
//		}
//
//		int memory = Integer.parseInt(planId.substring(planId.lastIndexOf("-")+1, planId.length()-1));
		JSONObject planExtra = JSONObject.fromObject(redisServiceRepo.getPlanExtra(planId));
//		Map<String, Object> containerInfoMap = redis.updateContainer(RestTemplateUtil.getRestTemplate(), instance.getContainerId(), planExtra);
//		
//		if(containerInfoMap != null){
//			instance = new ServiceInstance(
//					serviceInstanceId, 
//					service.getId(),
//					planId,
//					organizationGuid,
//					spaceGuid,
//					"http://redisadmin.tiger.mopaas.com",  //dashboard url is not implemented yet
//					containerInfoMap.get("containerId").toString(),
//					containerInfoMap.get("containerIp").toString(),
//					containerInfoMap.get("containerPort").toString(),
//					containerInfoMap.get("containerPasswd").toString()
//					);
		if(redis.updateContainer(RestTemplateUtil.getRestTemplate(), instance.getContainerId(),instance.getContainerHost(),  planExtra)){
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
			redisServiceRepo.updateInstance(serviceInstanceId, instance);			
		}
		return instance;
	}

}