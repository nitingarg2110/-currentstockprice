package com.stock.currentstockprice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

	@Bean
	public RestTemplate restTemplate() {

		RestTemplate restTemplate = new RestTemplate();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(0);
		requestFactory.setReadTimeout(0);

		restTemplate.setRequestFactory(requestFactory);

		return restTemplate;
	}

}
