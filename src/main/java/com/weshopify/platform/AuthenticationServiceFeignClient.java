package com.weshopify.platform;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import feign.Headers;
import feign.Param;

@FeignClient(name = "WESHOPIFY-CATEGORIES-SERVICE")
public interface AuthenticationServiceFeignClient {

	@Headers("Authorization: {access_token}")
	@GetMapping(value = "/validate-token")
	public String validateToken(@Param("access_token") String apiTokne);
}
