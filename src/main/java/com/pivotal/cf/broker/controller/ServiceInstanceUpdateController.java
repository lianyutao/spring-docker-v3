package com.pivotal.cf.broker.controller;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
import com.pivotal.cf.broker.model.ErrorMessage;
import com.pivotal.cf.broker.model.Previous;
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.model.UpdateServiceInstanceRequest;
import com.pivotal.cf.broker.model.UpdateServiceInstanceResponse;
import com.pivotal.cf.broker.mongodb.service.MongodbServiceInstanceService;
import com.pivotal.cf.broker.mysql.service.MysqlServiceInstanceService;
import com.pivotal.cf.broker.postgresql.service.PostgresqlServiceInstanceService;
import com.pivotal.cf.broker.redis.service.RedisServiceInstanceService;
import com.pivotal.cf.broker.repo.MemcachedPersistedServiceRepo;
import com.pivotal.cf.broker.repo.MongodbPersistedServiceRepo;
import com.pivotal.cf.broker.repo.MysqlPersistedServiceRepo;
import com.pivotal.cf.broker.repo.PostgresqlPersistedServiceRepo;
import com.pivotal.cf.broker.repo.RedisPersistedServiceRepo;
import com.pivotal.cf.broker.service.CatalogService;
import com.pivotal.cf.broker.util.EntryUtil;
import com.pivotal.cf.broker.util.JsonUtil;

/**
 * See: http://docs.cloudfoundry.com/docs/running/architecture/services/writing-service.html
 * 
 * @author sgreenberg@gopivotal.com
 */
@Controller
public class ServiceInstanceUpdateController extends BaseController {

	public static final String BASE_PATH = "/v2/service_instances";
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceInstanceUpdateController.class);
	
	private MysqlServiceInstanceService mysqlService;
	private PostgresqlServiceInstanceService postgresqlService;
	private MongodbServiceInstanceService mongodbService;
	private RedisServiceInstanceService redisService;
	private MemcachedServiceInstanceService memcachedService;
	private CatalogService catalogService;
	
	@Autowired
 	public ServiceInstanceUpdateController(MysqlServiceInstanceService mysqlService, 
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
	

	@RequestMapping(value = BASE_PATH+"/{instance_id}", method = RequestMethod.PATCH)
	public ResponseEntity<UpdateServiceInstanceResponse> updateServiceInstance(
			@PathVariable("instance_id") String serviceInstanceId,
			@Valid @RequestBody UpdateServiceInstanceRequest request,
			@RequestParam(value = "accepts_incomplete" , required = false) boolean acceptsIncomplete)throws
	ServiceDefinitionDoesNotExistException,
	ServiceInstanceExistsException,
	ServiceBrokerException, ServiceInstanceDoesNotExistException {
		
		logger.info("------------------request =" + JsonUtil.toJson(request));
		ServiceDefinition svc = catalogService.getServiceDefinition(request.getServiceDefinitionId());

		if (svc == null) {
			throw new ServiceDefinitionDoesNotExistException(request.getServiceDefinitionId());
		}
		
		ServiceInstance instance = null;
		String orgPlanId = "";
		String dstPlanId = "";
		switch(request.getServiceDefinitionId()){
		case "mysql-docker":
			orgPlanId = request.getPlanId();
			System.out.println("@@@@ prgPlanId = "+orgPlanId + " plan_id = " +request.getPlanId() );
			instance = mysqlService.updateServiceInstance(
					svc, 
					serviceInstanceId, 
					orgPlanId,
					request.getPrevious().getOrganizationId(),
					request.getPrevious().getSpaceId()
					);
			dstPlanId = request.getPrevious().getPlanId();
			break;
		case "mongodb-docker":
			orgPlanId = request.getPlanId();
			instance = mongodbService.updateServiceInstance(
					svc, 
					serviceInstanceId, 
					orgPlanId,
					request.getPrevious().getOrganizationId(),
					request.getPrevious().getSpaceId()
					);
			dstPlanId = request.getPrevious().getPlanId();
			break;
		case "postgresql-docker":
			orgPlanId = request.getPlanId();
			instance = postgresqlService.updateServiceInstance(
					svc, 
					serviceInstanceId, 
					orgPlanId,
					request.getPrevious().getOrganizationId(),
					request.getPrevious().getSpaceId()
					);
			dstPlanId = request.getPrevious().getPlanId();
			break;
		case "redis-docker":
			orgPlanId = request.getPlanId();
			instance = redisService.updateServiceInstance(
					svc, 
					serviceInstanceId, 
					orgPlanId,
					request.getPrevious().getOrganizationId(),
					request.getPrevious().getSpaceId()
					);
			dstPlanId = request.getPrevious().getPlanId();
			break;
		case "memcached-docker":
			orgPlanId = request.getPlanId();
			instance = memcachedService.updateServiceInstance(
					svc, 
					serviceInstanceId, 
					orgPlanId,
					request.getPrevious().getOrganizationId(),
					request.getPrevious().getSpaceId()
					);
			dstPlanId = request.getPrevious().getPlanId();
			break;
		default:
			logger.error("unknown service: " + request.getServiceDefinitionId());
		}
		logger.info("ServiceInstance udpate finished: " + instance.getId());
		
		if(orgPlanId.equals(dstPlanId)){
        return new ResponseEntity<UpdateServiceInstanceResponse>(
				new UpdateServiceInstanceResponse("501"),
				HttpStatus.NOT_IMPLEMENTED);
		} else{
			return new ResponseEntity<UpdateServiceInstanceResponse>(
	        		new UpdateServiceInstanceResponse("000"), 
	        		HttpStatus.OK);
		}
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
	
	public String replacePlanId(String orgPlanId, String type){
		String planId = "";
		switch(type){
		case "mysql":
			planId="mysql-docker-plan-" + orgPlanId.substring(orgPlanId.lastIndexOf(" ")+1);
			break;
		case "mongodb":
			planId="mongodb-docker-plan-" + orgPlanId.substring(orgPlanId.lastIndexOf(" ")+1);
			break;
		case "postgresql":
			planId="postgresql-docker-plan-" + orgPlanId.substring(orgPlanId.lastIndexOf(" ")+1);
			break;
		case "memcached":
			planId="memcached-docker-plan-" + orgPlanId.substring(orgPlanId.lastIndexOf(" ")+1);
			break;
		case "redis":
			planId="redis-docker-plan-" + orgPlanId.substring(orgPlanId.lastIndexOf(" ")+1);
			break;
		default:
			logger.error("replacePlanId failed");				
		}
		return planId;		
	}
	
}
