package com.pivotal.cf.broker.memcached.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pivotal.cf.broker.exception.ServiceBrokerException;
import com.pivotal.cf.broker.exception.ServiceInstanceBindingDoesNotExistException;
import com.pivotal.cf.broker.exception.ServiceInstanceBindingExistsException;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.model.ServiceInstanceBinding;
import com.pivotal.cf.broker.repo.MemcachedPersistedServiceRepo;
import com.pivotal.cf.broker.service.ServiceInstanceBindingService;

/**
 * Mongo impl to bind services.  Binding a service does the following:
 * creates a new user in the database (currently uses a default pwd of "password"),
 * saves the ServiceInstanceBinding info to the Mongo repository.
 *  
 * @author sgreenberg@gopivotal.com
 *
 */
@Service
public class MemcachedServiceInstanceBindingService implements ServiceInstanceBindingService {

	private MemcachedAdminService memcached; 
	@Autowired
	private MemcachedPersistedServiceRepo memcachedServiceRepo;
	
	@Autowired
	public MemcachedServiceInstanceBindingService(MemcachedAdminService memcached) {
		this.memcached = memcached;
	}
	
	@Override
	public ServiceInstanceBinding createServiceInstanceBinding(
			String bindingId, ServiceInstance serviceInstance,
			String serviceId, String planId, String appGuid)
			throws ServiceInstanceBindingExistsException, ServiceBrokerException {
		ServiceInstanceBinding binding = getServiceInstanceBinding(bindingId);;
		if (binding != null) {
			throw new ServiceInstanceBindingExistsException(binding);
		}
		
		Map<String,Object> credentials = new HashMap<String,Object>();
		credentials.put("id", serviceInstance.getContainerId());
		credentials.put("host", serviceInstance.getContainerHost());
		credentials.put("port", serviceInstance.getContainerPort());
		credentials.put("user", serviceInstance.getContainerUser());
		credentials.put("password", serviceInstance.getContainerPasswd());
		binding = new ServiceInstanceBinding(bindingId, serviceInstance.getId(), 
				credentials, "", appGuid);
		memcachedServiceRepo.addBinding(bindingId, binding);
		return binding;
	}

	@Override
	public ServiceInstanceBinding getServiceInstanceBinding(String id) {
		return memcachedServiceRepo.getBinding(id);
	}

	@Override
	public ServiceInstanceBinding deleteServiceInstanceBinding(String id) {
		ServiceInstanceBinding binding = getServiceInstanceBinding(id);
		if(binding == null) {
			try {
				throw new ServiceInstanceBindingDoesNotExistException(id);
			} catch (ServiceInstanceBindingDoesNotExistException e) {
				e.printStackTrace();
			}
		}
		
		memcachedServiceRepo.removeBinding(id);
		return binding;
	}
}
