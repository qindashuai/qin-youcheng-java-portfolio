package com.qinyoucheng.auth.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", System.currentTimeMillis());

        if (ex instanceof org.springframework.cloud.gateway.support.NotFoundException) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            result.put("code", 404);
            result.put("message", "服务未找到");
        } else if (ex instanceof org.springframework.web.server.ResponseStatusException) {
            org.springframework.web.server.ResponseStatusException rse = (org.springframework.web.server.ResponseStatusException) ex;
            response.setStatusCode(rse.getStatus());
            result.put("code", rse.getStatus().value());
            result.put("message", rse.getReason() != null ? rse.getReason() : "请求异常");
        } else {
            log.error("网关全局异常", ex);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            result.put("code", 500);
            result.put("message", "网关内部错误");
        }

        result.put("data", null);
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(result);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            String fallback = "{\"code\":500,\"message\":\"网关内部错误\",\"data\":null}";
            DataBuffer buffer = response.bufferFactory().wrap(fallback.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }
    }
}
