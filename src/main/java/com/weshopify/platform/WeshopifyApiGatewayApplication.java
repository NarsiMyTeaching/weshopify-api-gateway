package com.weshopify.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import reactor.core.publisher.Mono;

//@SpringBootApplication(exclude = {ReactiveSecurityAutoConfiguration.class, ReactiveManagementWebSecurityAutoConfiguration.class })
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class WeshopifyApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeshopifyApiGatewayApplication.class, args);
	}
	
	@Bean
	KeyResolver userKeyResolver() {
		return exchange -> Mono.just("1");
	}
	
	@Bean
	public HttpMessageConverters messageConverts() {
		return new HttpMessageConverters();
	}

}
