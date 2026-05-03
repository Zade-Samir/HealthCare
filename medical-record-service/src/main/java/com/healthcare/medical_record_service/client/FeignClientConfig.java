package com.healthcare.medical_record_service.client;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {

    @Value("${app.security.internal-secret-key}")
    private String internalSecretKey;

    @Bean
    public RequestInterceptor requestInterceptor() {

        return requestTemplate -> {

            //get the authorization header(JWT) from current request
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                String token = attributes.getRequest().getHeader("Authorization");
                if (token != null) {
                    requestTemplate.header("Authorization", token);
                }
            }

            //add the secret key to check for another-service
            requestTemplate.header("X-Internal-Secret", internalSecretKey);
        };
    }
}
