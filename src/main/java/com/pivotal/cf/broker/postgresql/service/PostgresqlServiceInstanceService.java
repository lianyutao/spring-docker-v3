package com.pivotal.cf.broker.postgresql.service;

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
import com.pivotal.cf.broker.repo.PostgresqlPersistedServiceRepo;
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
public class PostgresqlServiceInstanceService implements ServiceInstanceService {
//	public static Map<String, ServiceInstance> serviceInstances = new HashMap<String, ServiceInstance>();
	private Logger logger = LoggerFactory.getLogger(PostgresqlServiceInstanceService.class);
	private PostgresqlAdminService psql;
	@Autowired
	private PostgresqlPersistedServiceRepo postgresqlServiceRepo;
	
	@Autowired
	public PostgresqlServiceInstanceService(PostgresqlAdminService psql) {
		this.psql = psql;
	}
	
	@Override
	public List<ServiceInstance> getAllServiceInstances() {
		return postgresqlServiceRepo.getInstances();
	}

	@Override
	public ServiceInstance createServiceInstance(ServiceDefinition service,
			String serviceInstanceId, String planId, String organizationGuid,
			String spaceGuid) 
			throws ServiceInstanceExistsException, ServiceBrokerException {
		ServiceInstance instance = getServiceInstance(serviceInstanceId);
//		if (instance != null) {
//			throw new ServiceInstanceExistsException(instance);
//		}
//		int mountSize = Integer.parseInt(planId.substring(planId.lastIndexOf("-")+1, planId.length()-1));
		int mountSize = -1;
		JSONObject plan = JSONObject.fromObject(postgresqlServiceRepo.getPlanExtra(planId));
		logger.info("createServiceInstance plan extra "+ plan);
	

		
		Map<String, Object> containerInfoMap = psql.createContainer(RestTemplateUtil.getRestTemplate(),plan );
		
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
			postgresqlServiceRepo.addInstance(serviceInstanceId, instance);			
		}
		return instance;
	}

	@Override
	public ServiceInstance getServiceInstance(String id) {
		return postgresqlServiceRepo.getInstance(id);
	}

	@Override
	public ServiceInstance deleteServiceInstance(String id) {
		ServiceInstance instance = getServiceInstance(id);
		if(instance != null && !postgresqlServiceRepo.isInstanceBound(id)) {
//			if(psql.stopContainer(RestTemplateUtil.getRestTemplate(), instance.getContainerId(), instance.getContainerHost())){
				psql.deleteContainer(RestTemplateUtil.getRestTemplate(), instance.getContainerId(), instance.getContainerHost());
				postgresqlServiceRepo.removeInstance(id);	
				return instance;
			}
//		}
		
//		if(postgresqlServiceRepo.isInstanceBound(id)) {
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
//		int mountSize = Integer.parseInt(planId.substring(planId.lastIndexOf("-")+1, planId.length()-1));
		JSONObject planExtra = JSONObject.fromObject(postgresqlServiceRepo.getPlanExtra(planId));
		if(psql.updateContainer(RestTemplateUtil.getRestTemplate(), instance.getContainerId(),instance.getContainerHost(),  planExtra)){
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
			postgresqlServiceRepo.updateInstance(serviceInstanceId, instance);
		}
		return instance;
	}
}