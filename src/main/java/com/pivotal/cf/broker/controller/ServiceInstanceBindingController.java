package com.pivotal.cf.broker.controller;


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
import com.pivotal.cf.broker.exception.ServiceInstanceBindingExistsException;
import com.pivotal.cf.broker.exception.ServiceInstanceDoesNotExistException;
import com.pivotal.cf.broker.memcached.service.MemcachedServiceInstanceBindingService;
import com.pivotal.cf.broker.memcached.service.MemcachedServiceInstanceService;
import com.pivotal.cf.broker.model.ErrorMessage;
import com.pivotal.cf.broker.model.ServiceInstanceBinding;
import com.pivotal.cf.broker.model.ServiceInstanceBindingRequest;
import com.pivotal.cf.broker.model.ServiceInstanceBindingResponse;
import com.pivotal.cf.broker.mongodb.service.MongodbServiceInstanceBindingService;
import com.pivotal.cf.broker.mongodb.service.MongodbServiceInstanceService;
import com.pivotal.cf.broker.mysql.service.MysqlServiceInstanceBindingService;
import com.pivotal.cf.broker.mysql.service.MysqlServiceInstanceService;
import com.pivotal.cf.broker.postgresql.service.PostgresqlServiceInstanceBindingService;
import com.pivotal.cf.broker.postgresql.service.PostgresqlServiceInstanceService;
import com.pivotal.cf.broker.redis.service.RedisServiceInstanceBindingService;
import com.pivotal.cf.broker.redis.service.RedisServiceInstanceService;

/**
 * See: Source: http://docs.cloudfoundry.com/docs/running/architecture/services/writing-service.html
 * 
 * @author sgreenberg@gopivotal.com
 */
@Controller
public class ServiceInstanceBindingController extends BaseController {

	public static final String BASE_PATH = "/v2/service_instances/{instanceId}/service_bindings";
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceInstanceBindingController.class);
	
	private MysqlServiceInstanceBindingService mysqlServiceInstanceBindingService;
	private MysqlServiceInstanceService mysqlServiceInstanceService;
	private MongodbServiceInstanceBindingService mongodbServiceInstanceBindingService;
	private MongodbServiceInstanceService mongodbServiceInstanceService;
	private PostgresqlServiceInstanceBindingService postgresqlServiceInstanceBindingService;
	private PostgresqlServiceInstanceService postgresqlServiceInstanceService;
	private RedisServiceInstanceBindingService redisServiceInstanceBindingService;
	private RedisServiceInstanceService redisServiceInstanceService;
	private MemcachedServiceInstanceBindingService memcachedServiceInstanceBindingService;
	private MemcachedServiceInstanceService memcachedServiceInstanceService;
	
	@Autowired
	public ServiceInstanceBindingController(MysqlServiceInstanceBindingService mysqlServiceInstanceBindingService,
			MysqlServiceInstanceService mysqlServiceInstanceService,
			MongodbServiceInstanceBindingService mongodbServiceInstanceBindingService,
			MongodbServiceInstanceService mongodbServiceInstanceService,
			PostgresqlServiceInstanceBindingService postgresqlServiceInstanceBindingService,
			PostgresqlServiceInstanceService postgresqlServiceInstanceService,
			RedisServiceInstanceBindingService redisServiceInstanceBindingService,
			RedisServiceInstanceService redisServiceInstanceService,
			MemcachedServiceInstanceBindingService memcachedServiceInstanceBindingService,
			MemcachedServiceInstanceService memcachedServiceInstanceService
			) {
		this.mysqlServiceInstanceBindingService = mysqlServiceInstanceBindingService;
		this.mysqlServiceInstanceService = mysqlServiceInstanceService;
		this.mongodbServiceInstanceBindingService = mongodbServiceInstanceBindingService;
		this.mongodbServiceInstanceService = mongodbServiceInstanceService;
		this.postgresqlServiceInstanceBindingService = postgresqlServiceInstanceBindingService;
		this.postgresqlServiceInstanceService = postgresqlServiceInstanceService;
		this.redisServiceInstanceBindingService = redisServiceInstanceBindingService;
		this.redisServiceInstanceService = redisServiceInstanceService;
		this.memcachedServiceInstanceBindingService = memcachedServiceInstanceBindingService;
		this.memcachedServiceInstanceService = memcachedServiceInstanceService;
	}
	
	@RequestMapping(value = BASE_PATH + "/{bindingId}", method = RequestMethod.PUT)
	public ResponseEntity<ServiceInstanceBindingResponse> bindServiceInstance(
			@PathVariable("instanceId") String instanceId, 
			@PathVariable("bindingId") String bindingId,
			@Valid @RequestBody ServiceInstanceBindingRequest request) throws
			ServiceInstanceDoesNotExistException, ServiceInstanceBindingExistsException, 
			ServiceBrokerException {
		logger.debug( "PUT: " + BASE_PATH + "/{bindingId}"
				+ ", bindServiceInstance(), serviceInstance.id = " + instanceId 
				+ ", bindingId = " + bindingId);
		if (null == mysqlServiceInstanceService.getServiceInstance(instanceId) &&
			null == mongodbServiceInstanceService.getServiceInstance(instanceId) &&
			null == postgresqlServiceInstanceService.getServiceInstance(instanceId) &&
			null == redisServiceInstanceService.getServiceInstance(instanceId) &&
			null == memcachedServiceInstanceService.getServiceInstance(instanceId)){
			throw new ServiceInstanceDoesNotExistException(instanceId);
		}
		ServiceInstanceBinding binding = null;		
		switch(request.getServiceDefinitionId()){
		case "mysql-docker":
			binding = mysqlServiceInstanceBindingService.createServiceInstanceBinding(
					bindingId,
					mysqlServiceInstanceService.getServiceInstance(instanceId), 
					request.getServiceDefinitionId(),
					request.getPlanId(),
					request.getAppGuid());
			break;
		case "mongodb-docker":
			binding = mongodbServiceInstanceBindingService.createServiceInstanceBinding(
					bindingId,
					mongodbServiceInstanceService.getServiceInstance(instanceId), 
					request.getServiceDefinitionId(),
					request.getPlanId(),
					request.getAppGuid());
			break;
		case "postgresql-docker":
			binding = postgresqlServiceInstanceBindingService.createServiceInstanceBinding(
					bindingId,
					postgresqlServiceInstanceService.getServiceInstance(instanceId), 
					request.getServiceDefinitionId(),
					request.getPlanId(),
					request.getAppGuid());
			break;
		case "redis-docker":
			binding = redisServiceInstanceBindingService.createServiceInstanceBinding(
					bindingId,
					redisServiceInstanceService.getServiceInstance(instanceId), 
					request.getServiceDefinitionId(),
					request.getPlanId(),
					request.getAppGuid());
			break;
		case "memcached-docker":
			binding = memcachedServiceInstanceBindingService.createServiceInstanceBinding(
					bindingId,
					memcachedServiceInstanceService.getServiceInstance(instanceId), 
					request.getServiceDefinitionId(),
					request.getPlanId(),
					request.getAppGuid());
			break;
		default:
		}
		logger.debug("ServiceInstanceBinding Created: " + binding.getId());
        return new ResponseEntity<ServiceInstanceBindingResponse>(
        		new ServiceInstanceBindingResponse(binding), 
        		HttpStatus.CREATED);
	}
	
	@RequestMapping(value = BASE_PATH + "/{bindingId}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServiceInstanceBinding(
			@PathVariable("instanceId") String instanceId, 
			@PathVariable("bindingId") String bindingId,
			@RequestParam("service_id") String serviceId,
			@RequestParam("plan_id") String planId) throws ServiceBrokerException {
		logger.debug( "DELETE: " + BASE_PATH + "/{bindingId}"
				+ ", deleteServiceInstanceBinding(),  serviceInstance.id = " + instanceId 
				+ ", bindingId = " + bindingId 
				+ ", serviceId = " + serviceId
				+ ", planId = " + planId);
		ServiceInstanceBinding binding = null; 
		switch(serviceId){
		case "mysql-docker":
			binding = mysqlServiceInstanceBindingService.deleteServiceInstanceBinding(bindingId);
			break;
		case "mongodb-docker":
			binding = mongodbServiceInstanceBindingService.deleteServiceInstanceBinding(bindingId);
			break;
		case "postgresql-docker":
			binding = postgresqlServiceInstanceBindingService.deleteServiceInstanceBinding(bindingId);
			break;
		case "redis-docker":
			binding = redisServiceInstanceBindingService.deleteServiceInstanceBinding(bindingId);
			break;
		case "memcached-docker":
			binding = memcachedServiceInstanceBindingService.deleteServiceInstanceBinding(bindingId);
			break;
		default:
		}
		if (binding == null) {
			return new ResponseEntity<String>("{}", HttpStatus.NOT_FOUND);
		}
		logger.debug("ServiceInstanceBinding Deleted: " + binding.getId());
        return new ResponseEntity<String>("{}", HttpStatus.OK);
	}
	
	@ExceptionHandler(ServiceInstanceDoesNotExistException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			ServiceInstanceDoesNotExistException ex, 
			HttpServletResponse response) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(ServiceInstanceBindingExistsException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			ServiceInstanceBindingExistsException ex, 
			HttpServletResponse response) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
	}
	
}
