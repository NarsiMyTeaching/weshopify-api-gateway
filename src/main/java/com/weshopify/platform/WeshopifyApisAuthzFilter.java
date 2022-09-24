package com.weshopify.platform;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class WeshopifyApisAuthzFilter implements GlobalFilter {

	@Autowired
	private AuthenticationServiceFeignClient authnServiceFeign;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest serverRequest = exchange.getRequest();
		log.info("Request "+serverRequest.getPath().value()+" is Processing via authz filter");
		if(ApiUtils.allowedUrlsList.contains(serverRequest.getPath().value())) {
			return chain.filter(exchange);
		}
		try {
			String tokenWithBearer = serverRequest.getHeaders().getFirst(ApiUtils.TOKEN_HEADER_NAME);
			log.info("Incoming Request Authorization Token:\t"+tokenWithBearer);
			HttpHeaders headers = new HttpHeaders();
			headers.add(ApiUtils.TOKEN_HEADER_NAME, tokenWithBearer);
			
			String jsonResp = authnServiceFeign.validateToken(tokenWithBearer);
			log.info("validate token response is:\t"+jsonResp);
		}catch (Exception e) {
			e.printStackTrace();
			return onError(exchange, e, HttpStatus.BAD_REQUEST);
		}
		return chain.filter(exchange);
	}
	
	private Mono<Void> onError(ServerWebExchange exchange, Exception e, HttpStatus status ){
		ServerHttpResponse serverResp = exchange.getResponse();
		serverResp.setStatusCode(status);
		return serverResp.setComplete();
	}
	
	

}
