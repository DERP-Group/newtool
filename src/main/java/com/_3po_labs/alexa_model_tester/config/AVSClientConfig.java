package com._3po_labs.alexa_model_tester.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.threepio_labs.avsclient.client.AVSClient;

@Configuration
public class AVSClientConfig {

	@Bean
	public AVSClient avsClient(){
		return new AVSClient();
	}
}
