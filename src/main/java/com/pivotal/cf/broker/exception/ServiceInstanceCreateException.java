package com.pivotal.cf.broker.exception;


/**
 * Created by honine on 3/25/14.
 */
public class ServiceInstanceCreateException extends Exception {
	
    public String getMessage(String code) {
        return "create container failed! error NO. is  "
                + code;
    }
}
