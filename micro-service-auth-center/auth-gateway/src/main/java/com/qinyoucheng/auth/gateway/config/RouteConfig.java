package com.qinyoucheng.auth.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-system", r -> r
                        .path("/api/v1/auth/**", "/api/v1/users/**", "/api/v1/roles/**",
                                "/api/v1/menus/**", "/api/v1/data-permissions/**",
                                "/api/v1/blacklist/**", "/api/v1/logs/**")
                        .filters(f -> f
                                .stripPrefix(0)
                                .addRequestHeader("X-Gateway-Source", "auth-gateway")
                                .addResponseHeader("X-Response-Gateway", "auth-gateway"))
                        .uri("lb://auth-system"))
                .build();
    }
}
