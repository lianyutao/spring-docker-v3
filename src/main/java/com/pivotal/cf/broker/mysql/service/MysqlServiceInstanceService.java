package com.pivotal.cf.broker.mysql.service;

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
import com.pivotal.cf.broker.model.Plan;
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.repo.MysqlPersistedServiceRepo;
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
public class MysqlServiceInstanceService implements ServiceInstanceService {
//	public static Map<String, ServiceInstance> serviceInstances = new HashMap<String, ServiceInstance>();
	private Logger logger = LoggerFactory.getLogger(MysqlServiceInstanceService.class);
	private MysqlAdminService mysql;
	@Autowired
	private MysqlPersistedServiceRepo mysqlServiceRepo;
	
	@Autowired
	public MysqlServiceInstanceService(MysqlAdminService mysql) {
		this.mysql = mysql;
	}
	
	@Override
	public List<ServiceInstance> getAllServiceInstances() {
		return mysqlServiceRepo.getInstances();
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
//		int mountSize = -1;
//		if("mysql-docker-plan".equals(planId)){
//			mountSize = 25;
//		}else{
//			mountSize = Integer.parseInt(planId.substring(planId.lastIndexOf("-")+1, planId.length()-1));				
//		}
		
		JSONObject plan = JSONObject.fromObject(mysqlServiceRepo.getPlanExtra(planId));
		logger.info("createServiceInstance plan extra "+ plan);

		Map<String, Object> containerInfoMap = mysql.createContainer(RestTemplateUtil.getRestTemplate(), plan);
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
			mysqlServiceRepo.addInstance(serviceInstanceId, instance);			
		}
		return instance;
	}

	@Override
	public ServiceInstance getServiceInstance(String id) {
		return mysqlServiceRepo.getInstance(id);
	}

	@Override
	public ServiceInstance deleteServiceInstance(String id) {
		ServiceInstance instance = getServiceInstance(id);
		if(instance != null && !mysqlServiceRepo.isInstanceBound(id)) {

//			if(mysql.stopContainer(RestTemplateUtil.getRestTemplate(), instance.getContainerId(), instance.getContainerHost())){
				mysql.deleteContainer(RestTemplateUtil.getRestTemplate(), instance.getContainerId(), instance.getContainerHost());
				mysqlServiceRepo.removeInstance(id);
				return instance;
			}
//		}
		
//		if(mysqlServiceRepo.isInstanceBound(id)) {
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
			throws ServiceInstanceDoesNotExistException, ServiceBrokerException {
		ServiceInstance instance = getServiceInstance(serviceInstanceId);
		if (instance == null) {
			throw new ServiceInstanceDoesNotExistException(serviceInstanceId);
		}
//		int mountSize;
//		if("Default Mysql-docker Plan".equals(planId)){
//			mountSize = 25;
//		}else{
//			mountSize = Integer.parseInt(planId.substring(planId.lastIndexOf("-")+1, planId.length()-1));				
//		}
		JSONObject planExtra = JSONObject.fromObject(mysqlServiceRepo.getPlanExtra(planId));
		if(mysql.updateContainer(RestTemplateUtil.getRestTemplate(), instance.getContainerId(),instance.getContainerHost(),  planExtra)){
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
			mysqlServiceRepo.updateInstance(serviceInstanceId, instance);
		}
		return instance;
	}
	
}