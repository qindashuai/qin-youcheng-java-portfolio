package com.qinyoucheng.auth.gateway.filter;

import com.qinyoucheng.auth.common.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${gateway.white-list:/api/v1/auth/login,/api/v1/auth/sso/callback,/api/v1/auth/sso/ticket}")
    private List<String> whiteList;

    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.debug("网关鉴权过滤: path={}", path);

        if (isWhiteListed(path)) {
            log.debug("白名单路径放行: {}", path);
            return chain.filter(exchange);
        }

        String token = extractToken(request);
        if (token == null) {
            log.warn("Token缺失: path={}", path);
            return unauthorized(exchange, "Token缺失，请先登录");
        }

        try {
            if (!jwtUtil.validateToken(token)) {
                log.warn("Token无效: path={}", path);
                return unauthorized(exchange, "Token无效或已过期");
            }

            if (isTokenBlacklisted(token)) {
                log.warn("Token已在黑名单: path={}", path);
                return unauthorized(exchange, "Token已失效，请重新登录");
            }

            if (!isAccessTokenInRedis(token)) {
                log.warn("Token不在Redis会话中: path={}", path);
                return unauthorized(exchange, "会话已失效，请重新登录");
            }

            Claims claims = jwtUtil.parseToken(token);
            String userId = claims.getSubject();
            String username = claims.get("username", String.class);
            String appKey = claims.get("appKey", String.class);

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-Username", username)
                    .header("X-App-Key", appKey)
                    .build();

            log.debug("鉴权通过: userId={}, username={}, appKey={}, path={}", userId, username, appKey, path);
            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            log.error("Token解析异常: path={}, error={}", path, e.getMessage());
            return unauthorized(exchange, "Token解析失败");
        }
    }

    private boolean isWhiteListed(String path) {
        return whiteList.stream().anyMatch(white -> {
            if (white.endsWith("/**")) {
                return path.startsWith(white.substring(0, white.length() - 3));
            }
            return path.equals(white);
        });
    }

    private String extractToken(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith(TOKEN_PREFIX)) {
            return authorization.substring(TOKEN_PREFIX.length());
        }
        String token = request.getQueryParams().getFirst("token");
        if (token != null && !token.isEmpty()) {
            return token;
        }
        return null;
    }

    private boolean isTokenBlacklisted(String token) {
        String key = "auth:blacklist:token:" + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private boolean isAccessTokenInRedis(String token) {
        String key = "auth:token:" + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"code\":401,\"message\":\"" + message + "\",\"data\":null,\"timestamp\":" + System.currentTimeMillis() + "}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
