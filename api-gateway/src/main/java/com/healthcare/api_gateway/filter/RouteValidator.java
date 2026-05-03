package com.healthcare.api_gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest; //spring cloud gateway is run on reactive architecture, hence use this in place of servlet
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

//decide with path should be authenticated or which not
@Component
public class RouteValidator {

    public static final List<String> openAPIEndpoints = List.of(
            "/auth/register",
            "/auth/login",
            "/eureka"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request ->
                    openAPIEndpoints.stream()
                    .noneMatch(uri -> request.getURI()
                            .getPath()
                            .contains(uri));
}
