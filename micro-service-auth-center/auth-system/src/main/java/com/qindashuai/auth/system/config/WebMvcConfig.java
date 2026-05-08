package com.qindashuai.auth.system.config;

import com.qindashuai.auth.common.UserContext;
import com.qindashuai.auth.common.UserContextHolder;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@Configuration
public class WebMvcConfig implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {

    @Override
    public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                String userIdStr = request.getHeader("X-User-Id");
                String username = request.getHeader("X-Username");
                String appKey = request.getHeader("X-App-Key");

                if (userIdStr != null && !userIdStr.isEmpty()) {
                    UserContext context = new UserContext();
                    context.setUserId(Long.parseLong(userIdStr));
                    context.setUsername(username);
                    context.setAppKey(appKey);
                    context.setIp(getClientIp(request));
                    UserContextHolder.set(context);
                }
                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
                UserContextHolder.clear();
            }

            private String getClientIp(HttpServletRequest request) {
                String ip = request.getHeader("X-Forwarded-For");
                if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                    int index = ip.indexOf(',');
                    return index != -1 ? ip.substring(0, index) : ip;
                }
                ip = request.getHeader("X-Real-IP");
                if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                    return ip;
                }
                return request.getRemoteAddr();
            }
        }).addPathPatterns("/api/v1/**")
          .excludePathPatterns("/api/v1/auth/login", "/api/v1/auth/sso/**", "/api/v1/auth/refresh");
    }
}
