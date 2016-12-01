package com.pivotal.cf.broker.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.model.ServiceInstanceBinding;
import com.pivotal.cf.broker.util.Utils;

@Component
public class RedisPersistedServiceRepo implements ServiceRepo {
	private Logger logger = LoggerFactory.getLogger(RedisPersistedServiceRepo.class);
	
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplateccdb;

    // Service Instance Queries
    private static final String addInstanceSQL = "INSERT INTO redisdockerserviceinstance " +
            "(SERVICE_INSTANCE_ID, SERVICE_ID, PLAN_ID, ORGANIZATION_GUID, SPACE_GUID, DASHBOARD_URL, "
            + "CONTAINER_ID, CONTAINER_HOST, CONTAINER_PORT, CONTAINER_PASSWD) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String removeInstanceSQL = "DELETE FROM redisdockerserviceinstance " +
            "WHERE SERVICE_INSTANCE_ID = ?";
    private static final String getInstanceSQL = "SELECT * FROM redisdockerserviceinstance " +
            "WHERE SERVICE_INSTANCE_ID = ?";
    private static final String getInstancesSQL = "SELECT * FROM redisdockerserviceinstance";
    private static final String updateInstancesSQL = "update redisdockerserviceinstance set PLAN_ID=?, CONTAINER_ID=? "
    		+ "where SERVICE_INSTANCE_ID=?";
    //ccdb
    private static final String updateSQLccdb = "update service_instances set service_plan_id="
    		+ "(select id from service_plans where unique_id=?) where guid=?";
    
    //ccdb t_meterage
    private static final String getOrgName = "select name from organizations where guid=?";
    private static final String getSpaceName = "select name from spaces where guid=?";
    private static final String getResourceName = "select name from service_instances where guid=?";
    
    private static final String addNewRecord = "insert into t_meterage (org_guid, org_name, space_guid, space_name, "
    		+ "reource_type, resource_guid,  cpu, disk, memory, traffic, "
    		+ "created_at, updated_at) values (?,?,?,?,?,?,?,?,?,?,now(),now())";
    
    private static final String updatelastedat = "update t_meterage set lasted_at=now() where id=(select id from "
    		+ "t_meterage where lasted_at is null and resource_guid=?)";
    
    private static final String updateResouceName = "update t_meterage set resource_name=? where id=(select id from "
    		+ "t_meterage where lasted_at is null and resource_guid=?)";
    
    private static final String getPlanExtraSQL = "select extra from service_plans where unique_id=?";

    // Service Instance Binding Queries
    private static final String addBindingSQL = "INSERT INTO ServiceInstanceBinding" +
            "(SERVICE_INSTANCE_BINDING_ID, SERVICE_INSTANCE_ID, CREDENTIALS, SYSLOG_DRAIN_URL, APP_GUID) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String removeBindingSQL = "DELETE FROM ServiceInstanceBinding " +
            "WHERE SERVICE_INSTANCE_BINDING_ID = ?";
    private static final String getBindingSQL = "SELECT * FROM ServiceInstanceBinding " +
            "WHERE SERVICE_INSTANCE_BINDING_ID = ?";
    private static final String getBindingsSQL = "SELECT * FROM ServiceInstanceBinding";

    private static final String isBoundSQL = "SELECT COUNT(*) FROM ServiceInstanceBinding " +
            "WHERE SERVICE_INSTANCE_ID = ?";

    /*
     * Service Instances CRUD
     */
    @Override
    public ServiceInstance addInstance(String key, ServiceInstance instance) {
        jdbcTemplate.update(addInstanceSQL, new Object[] {
                key,
                instance.getServiceDefinitionId(),
                instance.getPlanId(),
                instance.getOrganizationGuid(),
                instance.getSpaceGuid(),
                instance.getDashboardUrl(),
                instance.getContainerId(),
                instance.getContainerHost(),
                instance.getContainerPort(),
                instance.getContainerPasswd()
        });
        //获取组织名
        String orgName = jdbcTemplateccdb.queryForObject(getOrgName, new Object[]{instance.getOrganizationGuid()}, java.lang.String.class);
        //获取空间名
        String spaceName = jdbcTemplateccdb.queryForObject(getSpaceName, new Object[]{instance.getSpaceGuid()}, java.lang.String.class);
       //获取服务名
//        String resourceName = jdbcTemplateccdb.queryForObject(getResourceName,new Object[]{key},java.lang.String.class);
        //获取配额
        String planId = instance.getPlanId();
        
        JSONObject planExtra = JSONObject.fromObject(this.getPlanExtra(planId));
        int disk =0;
        int memory =0;
        double cpu =0;
        if(planExtra.get("diskVal")!=null && !planExtra.get("diskVal").equals("0")){
        	disk = Integer.parseInt((String) planExtra.get("diskVal"));
        }
        if(planExtra.get("memoryVal")!=null && !planExtra.get("memoryVal").equals("0")){
        	memory = Integer.parseInt((String) planExtra.get("memoryVal"));
        }
        if(planExtra.get("cpu")!=null && !planExtra.get("cpu").equals("0")){
        	cpu = Double.parseDouble((String) planExtra.get("cpu"));
        }
//        jdbcTemplateccdb.update(addNewRecord, new Object[]{
//        		instance.getOrganizationGuid(),
//        		orgName,
//        		instance.getSpaceGuid(),
//        		spaceName,
//        		"1",
//        		key,
//        		0,
//        		0,
//        		Integer.parseInt(planId.substring(planId.lastIndexOf('-')+1, planId.length()-1)),
//        		0,
//        });
      jdbcTemplateccdb.update(addNewRecord, new Object[]{
		instance.getOrganizationGuid(),
		orgName,
		instance.getSpaceGuid(),
		spaceName,
		"1",
		key,
		memory,
		cpu,
		disk,
		0,
});
        return instance;
    }

    public ServiceInstance updateInstance(String key, ServiceInstance instance) {
        jdbcTemplate.update(updateInstancesSQL, new Object[] {
                instance.getPlanId(),
                instance.getContainerId(),
                key
        });
//        jdbcTemplateccdb.update(updateSQLccdb, new Object[]{
//        		instance.getPlanId(),
//        		key
//        });
        //获取组织名
        String orgName = jdbcTemplateccdb.queryForObject(getOrgName, new Object[]{instance.getOrganizationGuid()}, java.lang.String.class);
        //获取空间名
        String spaceName = jdbcTemplateccdb.queryForObject(getSpaceName, new Object[]{instance.getSpaceGuid()}, java.lang.String.class);
        //获取服务名
//        String resourceName = jdbcTemplateccdb.queryForObject(getResourceName,new Object[]{key},java.lang.String.class);
        //获取配额
        String planId = instance.getPlanId();
        jdbcTemplateccdb.update(updatelastedat,new Object[]{key});
        JSONObject planExtra = JSONObject.fromObject(this.getPlanExtra(planId));
        int disk =0;
        int memory =0;
        double cpu =0;
        if(planExtra.get("diskVal")!=null && !planExtra.get("diskVal").equals("0")){
        	disk = Integer.parseInt((String) planExtra.get("diskVal"));
        }
        if(planExtra.get("memoryVal")!=null && !planExtra.get("memoryVal").equals("0")){
        	memory = Integer.parseInt((String) planExtra.get("memoryVal"));
        }
        if(planExtra.get("cpu")!=null && !planExtra.get("cpu").equals("0")){
        	cpu = Double.parseDouble((String) planExtra.get("cpu"));
        }
//        jdbcTemplateccdb.update(addNewRecord, new Object[]{
//        		instance.getOrganizationGuid(),
//        		orgName,
//        		instance.getSpaceGuid(),
//        		spaceName,
//        		"1",
//        		key,
//        		0,
//        		0,
//        		Integer.parseInt(planId.substring(planId.lastIndexOf('-')+1, planId.length()-1)),
//        		0,
//        });
      jdbcTemplateccdb.update(addNewRecord, new Object[]{
		instance.getOrganizationGuid(),
		orgName,
		instance.getSpaceGuid(),
		spaceName,
		"1",
		key,
		memory,
		cpu,
		disk,
		0,
});
        return instance;
    }
    
    @Override
    public ServiceInstance removeInstance(String key) {
        ServiceInstance instance = getInstance(key);
        String resourceName = jdbcTemplateccdb.queryForObject(getResourceName, new Object[]{key}, java.lang.String.class);
        jdbcTemplateccdb.update(updateResouceName, new Object[]{resourceName, key});
        jdbcTemplate.update(removeInstanceSQL, new Object[]{key});
        jdbcTemplateccdb.update(updatelastedat,new Object[]{key});
        return instance;
    }

    @Override
    public ServiceInstance getInstance(String key) {
        ServiceInstance instance = null;
        try {
            instance = jdbcTemplate.queryForObject(getInstanceSQL, new Object[] {key}, new ServiceInstanceRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            // finding nothing is fine
        }
        return instance;
    }

    @Override
    public List<ServiceInstance> getInstances() {
        return jdbcTemplate.query(getInstancesSQL, new ServiceInstanceRowMapper());
    }

    @Override
    public boolean isInstanceBound(String key) {
        return jdbcTemplate.queryForObject(isBoundSQL, Integer.class, key) > 0;
    }

    private class ServiceInstanceRowMapper implements RowMapper<ServiceInstance> {
        @Override
        public ServiceInstance mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ServiceInstance(
                    rs.getString("SERVICE_INSTANCE_ID"),
                    rs.getString("SERVICE_ID"),
                    rs.getString("PLAN_ID"),
                    rs.getString("ORGANIZATION_GUID"),
                    rs.getString("SPACE_GUID"),
                    rs.getString("DASHBOARD_URL"),
                    rs.getString("CONTAINER_ID"),
                    rs.getString("CONTAINER_HOST"),
                    rs.getString("CONTAINER_PORT"),
                    rs.getString("CONTAINER_PASSWD")
                    );
        }
    }

    /*
     * Service Instance Bindings CRUD
     */

    @Override
    public ServiceInstanceBinding addBinding(String key, ServiceInstanceBinding binding) {
        jdbcTemplate.update(addBindingSQL, new Object[] {
                key,
                binding.getServiceInstanceId(),
                Utils.mapToJson(binding.getCredentials()),
                binding.getSyslogDrainUrl(),
                binding.getAppGuid()
        });
        return binding;
    }

    @Override
    public ServiceInstanceBinding removeBinding(String key) {
        ServiceInstanceBinding binding = getBinding(key);
        jdbcTemplate.update(removeBindingSQL, new Object[]{key});
        return binding;
    }

    @Override
    public ServiceInstanceBinding getBinding(String key) {
        ServiceInstanceBinding binding = null;
        try {
            binding = jdbcTemplate.queryForObject(getBindingSQL, new Object[]{key}, new ServiceInstanceBindingRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            // finding nothing is fine
        }
        return binding;
    }

    @Override
    public List<ServiceInstanceBinding> getBindings() {
        return jdbcTemplate.query(getBindingsSQL, new ServiceInstanceBindingRowMapper());
    }

    private class ServiceInstanceBindingRowMapper implements RowMapper<ServiceInstanceBinding> {
        @Override
        public ServiceInstanceBinding mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ServiceInstanceBinding(
                    rs.getString("SERVICE_INSTANCE_BINDING_ID"),
                    rs.getString("SERVICE_INSTANCE_ID"),
                    Utils.jsonToMap(rs.getString("CREDENTIALS")),
                    rs.getString("SYSLOG_DRAIN_URL"),
                    rs.getString("APP_GUID"));
        }
    }

	@Override
	public String getPlanExtra(String key) {
		return jdbcTemplateccdb.queryForObject(getPlanExtraSQL, new Object[]{key}, java.lang.String.class);
	}

}
