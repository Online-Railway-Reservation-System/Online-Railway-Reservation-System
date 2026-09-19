package com.railway.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class OpenApiServersGlobalFilter implements GlobalFilter, Ordered {

    private final ModifyResponseBodyGatewayFilterFactory modifyResponseBodyFactory;

    public OpenApiServersGlobalFilter(ModifyResponseBodyGatewayFilterFactory modifyResponseBodyFactory) {
        this.modifyResponseBodyFactory = modifyResponseBodyFactory;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.contains("/v3/api-docs") && !path.contains("swagger-config")) {
            ModifyResponseBodyGatewayFilterFactory.Config config = new ModifyResponseBodyGatewayFilterFactory.Config()
                    .setInClass(String.class)
                    .setOutClass(String.class)
                    .setRewriteFunction(String.class, String.class, (ex, body) -> {
                        if (body == null || body.isBlank()) {
                            return Mono.empty();
                        }
                        if (ex.getResponse().getStatusCode() != null && ex.getResponse().getStatusCode().isError()) {
                            return Mono.just(body);
                        }
                        String modified = body;
                        if (modified.contains("\"servers\"")) {
                            if (!modified.contains("\"bearerAuth\"")) {
                                modified = modified.replaceFirst("(?s)\"servers\"\\s*:\\s*\\[.*?\\]",
                                        "\"servers\":[{\"url\":\"/\",\"description\":\"API Gateway\"}],\"security\":[{\"bearerAuth\":[]}]");
                                if (modified.contains("\"components\":{")) {
                                    modified = modified.replaceFirst("\"components\":\\{",
                                            "\"components\":{\"securitySchemes\":{\"bearerAuth\":{\"type\":\"http\",\"scheme\":\"bearer\",\"bearerFormat\":\"JWT\"}},");
                                }
                            } else {
                                modified = modified.replaceFirst("(?s)\"servers\"\\s*:\\s*\\[.*?\\]",
                                        "\"servers\":[{\"url\":\"/\",\"description\":\"API Gateway\"}]");
                            }
                        }
                        return Mono.just(modified);
                    });
            return modifyResponseBodyFactory.apply(config).filter(exchange, chain);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
    }
}