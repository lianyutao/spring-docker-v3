package com.pivotal.cf.broker.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pivotal.cf.broker.exception.ServiceBrokerException;
import com.pivotal.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import com.pivotal.cf.broker.exception.ServiceInstanceDoesNotExistException;
import com.pivotal.cf.broker.exception.ServiceInstanceExistsException;
import com.pivotal.cf.broker.memcached.service.MemcachedServiceInstanceService;
import com.pivotal.cf.broker.model.CreateServiceInstanceRequest;
import com.pivotal.cf.broker.model.CreateServiceInstanceResponse;
import com.pivotal.cf.broker.model.ErrorMessage;
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.mongodb.service.MongodbServiceInstanceService;
import com.pivotal.cf.broker.mysql.service.MysqlServiceInstanceService;
import com.pivotal.cf.broker.postgresql.service.PostgresqlServiceInstanceService;
import com.pivotal.cf.broker.redis.service.RedisServiceInstanceService;
import com.pivotal.cf.broker.service.CatalogService;

/**
 * See: http://docs.cloudfoundry.com/docs/running/architecture/services/writing-service.html
 * 
 * @author sgreenberg@gopivotal.com
 */
@Controller
public class ServiceInstanceController extends BaseController {

	public static final String BASE_PATH = "/v2/service_instances";
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceInstanceController.class);
	
	private MysqlServiceInstanceService mysqlService;
	private PostgresqlServiceInstanceService postgresqlService;
	private MongodbServiceInstanceService mongodbService;
	private RedisServiceInstanceService redisService;
	private MemcachedServiceInstanceService memcachedService;
	private CatalogService catalogService;
	
	@Autowired
 	public ServiceInstanceController(MysqlServiceInstanceService mysqlService, 
 			PostgresqlServiceInstanceService postgresqlService,
 			MongodbServiceInstanceService mongodbService,
 			RedisServiceInstanceService redisService,
 			MemcachedServiceInstanceService memcachedService,
 			CatalogService catalogService) {
 		this.mysqlService = mysqlService;
 		this.postgresqlService = postgresqlService;
 		this.mongodbService = mongodbService;
 		this.redisService = redisService;
 		this.memcachedService = memcachedService; 
 		this.catalogService = catalogService;
 	}
	
	@RequestMapping(value = BASE_PATH, method = RequestMethod.GET)
	public @ResponseBody List<ServiceInstance> getServiceInstances() {
		logger.debug("GET: " + BASE_PATH + ", getServiceInstances()");
		ArrayList<ServiceInstance> allInstanceList = new ArrayList<ServiceInstance>();
		allInstanceList.addAll(mysqlService.getAllServiceInstances());
		allInstanceList.addAll(mongodbService.getAllServiceInstances());
		allInstanceList.addAll(postgresqlService.getAllServiceInstances());
		allInstanceList.addAll(redisService.getAllServiceInstances());
		allInstanceList.addAll(memcachedService.getAllServiceInstances());
		return allInstanceList;
	}
		
	@RequestMapping(value = BASE_PATH + "/{instanceId}", method = RequestMethod.PUT)
	public ResponseEntity<CreateServiceInstanceResponse> createServiceInstance(
			@PathVariable("instanceId") String serviceInstanceId, 
			@Valid @RequestBody CreateServiceInstanceRequest request) throws
			ServiceDefinitionDoesNotExistException,
			ServiceInstanceExistsException,
			ServiceBrokerException {
		logger.debug("PUT: " + BASE_PATH + "/{instanceId}" 
				+ ", createServiceInstance(), serviceInstanceId = " + serviceInstanceId);
		ServiceDefinition svc = catalogService.getServiceDefinition(request.getServiceDefinitionId());
		logger.info("-------request.getServiceDefinitionId():" + request.getServiceDefinitionId());
		logger.info("-------request.getPlanId():" + request.getPlanId());
		if (svc == null) {
			throw new ServiceDefinitionDoesNotExistException(request.getServiceDefinitionId());
		}
		ServiceInstance instance = null;
		switch(svc.getId()){
		case "mysql-docker":
			instance = mysqlService.createServiceInstance(
					svc, 
					serviceInstanceId, 
					request.getPlanId(),
					request.getOrganizationGuid(), 
					request.getSpaceGuid());
			break;
		case "mongodb-docker":
			instance = mongodbService.createServiceInstance(
					svc, 
					serviceInstanceId, 
					request.getPlanId(),
					request.getOrganizationGuid(), 
					request.getSpaceGuid());
			break;
		case "postgresql-docker":
			instance = postgresqlService.createServiceInstance(
					svc, 
					serviceInstanceId, 
					request.getPlanId(),
					request.getOrganizationGuid(), 
					request.getSpaceGuid());
			break;
		case "redis-docker":
			instance = redisService.createServiceInstance(
					svc, 
					serviceInstanceId, 
					request.getPlanId(),
					request.getOrganizationGuid(), 
					request.getSpaceGuid());
			break;
		case "memcached-docker":
			instance = memcachedService.createServiceInstance(
					svc, 
					serviceInstanceId, 
					request.getPlanId(),
					request.getOrganizationGuid(), 
					request.getSpaceGuid());
			break;
		default:
		}
		logger.debug("ServiceInstance Created: " + instance.getId());
        return new ResponseEntity<CreateServiceInstanceResponse>(
        		new CreateServiceInstanceResponse(instance), 
        		HttpStatus.CREATED);
	}
	
	@RequestMapping(value = BASE_PATH + "/{instanceId}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServiceInstance(
			@PathVariable("instanceId") String instanceId, 
			@RequestParam("service_id") String serviceId,
			@RequestParam("plan_id") String planId) throws ServiceBrokerException, ServiceInstanceDoesNotExistException {
		logger.debug( "DELETE: " + BASE_PATH + "/{instanceId}" 
				+ ", deleteServiceInstanceBinding(), serviceInstanceId = " + instanceId 
				+ ", serviceId = " + serviceId
				+ ", planId = " + planId);
		ServiceInstance instance = null; 
		switch(serviceId){
		case "mysql-docker":
			instance = mysqlService.deleteServiceInstance(instanceId);
			break;
		case "mongodb-docker":
			instance = mongodbService.deleteServiceInstance(instanceId);
			break;
		case "postgresql-docker":
			instance = postgresqlService.deleteServiceInstance(instanceId);
			break;
		case "redis-docker":
			instance = redisService.deleteServiceInstance(instanceId);
			break;
		case "memcached-docker":
			instance = memcachedService.deleteServiceInstance(instanceId);
			break;
		default:
			logger.error("unknown service: " + serviceId);
				
		}
		if (instance != null) {
			logger.debug("ServiceInstance Deleted: " + instance.getId());
		}
        return new ResponseEntity<String>("{}", HttpStatus.OK);
	}
	
	@ExceptionHandler(ServiceDefinitionDoesNotExistException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			ServiceDefinitionDoesNotExistException ex, 
			HttpServletResponse response) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(ServiceInstanceExistsException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			ServiceInstanceExistsException ex, 
			HttpServletResponse response) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}
	
}
