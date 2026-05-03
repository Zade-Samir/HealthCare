package com.healthcare.api_gateway.filter;

import com.healthcare.api_gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator validator;
    private final JwtUtil jwtUtil;

    public AuthenticationFilter(RouteValidator validator, JwtUtil jwtUtil) {
        super(Config.class);
        this.validator = validator;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (
                (exchange, chain) -> {

                    if (validator.isSecured.test(exchange.getRequest())) {
                        if (!exchange.getRequest().getHeaders()
                                .containsKey(HttpHeaders.AUTHORIZATION)) {
                            throw new RuntimeException("Missing authorization header");
                        }

                        String authHeader = exchange.getRequest()
                                .getHeaders()
                                .get(HttpHeaders.AUTHORIZATION).get(0);
                        if(authHeader != null && authHeader.startsWith("Bearer ")) {
                            authHeader = authHeader.substring(7);
                        }

                        try {
                            jwtUtil.validateToken(authHeader);
                        }
                        catch (Exception e) {
                            throw new RuntimeException("Unauthorized access to application");
                        }
                    }
                    return chain.filter(exchange);
                }
                );
    }

    public static class Config {

    }


}
