package com.railway.gateway.filter;

import com.railway.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    private static final List<String> OPEN_ENDPOINTS = List.of(
            "/api/v1/auth",
            "/api/v1/trains",
            "/api/v1/stations",
            "/api/v1/routes",
            "/api/v1/schedules",
            "/api/v1/fares",
            "/api/v1/search",
            "/api/v1/inventory",
            "/api/v1/food/menu",
            "/api/v1/tickets",
            "/actuator",
            "/v3/api-docs",
            "/swagger-ui"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();

        // Allow all auth endpoints and GET requests for public catalogs
        boolean isPublic = isOpenEndpoint(path, method);

        if (isPublic) {
            return chain.filter(exchange);
        }

        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return onError(exchange, HttpStatus.UNAUTHORIZED, "Missing Authorization Header");
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || authHeader.isBlank()) {
            return onError(exchange, HttpStatus.UNAUTHORIZED, "Missing Authorization Header");
        }

        String token = authHeader.trim();
        while (token.regionMatches(true, 0, "Bearer ", 0, 7)) {
            token = token.substring(7).trim();
        }

        if (!jwtUtil.isTokenValid(token)) {
            return onError(exchange, HttpStatus.UNAUTHORIZED, "Invalid or Expired JWT Token");
        }

        try {
            Claims claims = jwtUtil.extractAllClaims(token);
            String username = claims.getSubject();
            String userId = claims.get("userId") != null ? claims.get("userId").toString() : "";
            String roles = claims.get("roles") != null ? claims.get("roles").toString() : "";

            // If path requires ADMIN, check role
            if (path.contains("/admin/") && !roles.contains("ADMIN") && !roles.contains("ROLE_ADMIN")) {
                return onError(exchange, HttpStatus.FORBIDDEN, "Access Denied: Requires ADMIN role");
            }

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Email", username)
                    .header("X-User-Id", userId)
                    .header("X-User-Roles", roles)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            return onError(exchange, HttpStatus.UNAUTHORIZED, "JWT processing error: " + e.getMessage());
        }
    }

    private boolean isOpenEndpoint(String path, String method) {
        if (path.startsWith("/api/v1/auth/") || path.startsWith("/actuator") || path.contains("swagger") || path.contains("api-docs") || path.contains("webjars")) {
            return true;
        }
        if ("GET".equalsIgnoreCase(method)) {
            for (String openPath : OPEN_ENDPOINTS) {
                if (path.startsWith(openPath) && !path.contains("/admin/")) {
                    return true;
                }
            }
        }
        return false;
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String errorJson = String.format("{\"timestamp\":%d,\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
                System.currentTimeMillis(), status.value(), status.getReasonPhrase(), message, exchange.getRequest().getURI().getPath());
        byte[] bytes = errorJson.getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return -1; // Run after correlation ID filter
    }
}