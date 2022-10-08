package com.weshopify.platform;


import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
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
	private RedisTemplate<Object,Object> redisTemplate;

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
			
			HashOperations<Object, Object, Object> hashMap = redisTemplate.opsForHash();
			String tokenWithoutBearer = tokenWithBearer.replace("Bearer ", "");
			log.info("token without bearer:\t"+tokenWithoutBearer);
			String loggedInUser = (String) hashMap.get("weshopifyUser",tokenWithoutBearer);
			log.info("logged in user is:\t"+loggedInUser);
			if(loggedInUser != null && !loggedInUser.isEmpty()) {
				Date tokenExpiry =  (Date) hashMap.get("token_expiry",tokenWithoutBearer);
				System.out.println("token expiry is:\t"+tokenExpiry);
				boolean isTokenValid = new Date().before(tokenExpiry);
				if(isTokenValid) {
					log.info("token is valid");
					return chain.filter(exchange);					
				}else {
					return onError(exchange, new RuntimeException("token is invalid"), HttpStatus.BAD_REQUEST);
				}
			}else {
				return onError(exchange, new RuntimeException("forbidded"), HttpStatus.FORBIDDEN);
			}
			
			
			//String resp = authnServiceFeign.validateToken(tokenWithBearer);
			
			
		}catch (Exception e) {
			e.printStackTrace();
			return onError(exchange, e, HttpStatus.BAD_REQUEST);
		}
	
	}
	
	private Mono<Void> onError(ServerWebExchange exchange, Exception e, HttpStatus status ){
		ServerHttpResponse serverResp = exchange.getResponse();
		serverResp.setStatusCode(status);
		return serverResp.setComplete();
	}
	
	

}
