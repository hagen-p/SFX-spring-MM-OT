package com.sfx.ph.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import brave.sampler.Sampler;
import brave.SpanCustomizer;

@SpringBootApplication
public class Microservice1Application {

	
	public static void main(String[] args) {
		SpringApplication.run(Microservice1Application.class, args);
	}

@Bean
	public Sampler defaultSampler() {
		return Sampler.ALWAYS_SAMPLE;
	}
}
@RestController
class Microservice1Controller {
	
	@Autowired 
	SpanCustomizer span;	
	
	@Autowired
	RestTemplate restTemplate;

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	// setting up some fields for span.tags
	private String sVersion =  "1.1";   	 // example fields that will be passed as tags
    private String SCustomerType =  "Gold";  // example fields that will be passed as tags

	@GetMapping(value = "/microservice1")
	public String remotecall() {
		
		span.tag("Version", sVersion);  // sending tag along in the span. usefull for development
		span.tag("CustomerType",SCustomerType);
		LOG.info("Inside remotecall");
		String baseUrl = "http://localhost:8081/microservice2";
		String response = (String) restTemplate.exchange(baseUrl, HttpMethod.GET, null, String.class).getBody();
		LOG.info("The response recieved by the remote call is " + response);
		span.tag("Resopnse",response); //capture the response in a tag as well
		return response;
	}
}
