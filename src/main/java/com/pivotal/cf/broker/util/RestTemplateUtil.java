package com.pivotal.cf.broker.util;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestTemplateUtil {
	
	public static RestTemplate getRestTemplate(){
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(600000);
		requestFactory.setReadTimeout(600000);
		return new RestTemplate(requestFactory);
	}
}
