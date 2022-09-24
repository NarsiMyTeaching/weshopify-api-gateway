package com.weshopify.platform;

import java.util.Arrays;
import java.util.List;

public interface ApiUtils {

	String TOKEN_HEADER_NAME = "Authorization";
	String JWT_TOKEN_PREFIX = "Bearer";
	String JWT_TOKEN_TYPE = "Bearer";
	
	List<String> allowedUrlsList = Arrays.asList("/authn","/validate-token");
}
