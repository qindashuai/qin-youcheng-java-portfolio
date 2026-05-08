package com.qinyoucheng.toolkit.trace;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class TraceIdFilter implements Filter {

    private static final String TRACE_ID = "traceId";
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("TraceIdFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String traceId = httpRequest.getHeader(TRACE_ID_HEADER);
        if (!StringUtils.hasText(traceId)) {
            traceId = generateTraceId();
        }

        MDC.put(TRACE_ID, traceId);
        httpResponse.setHeader(TRACE_ID_HEADER, traceId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID);
        }
    }

    @Override
    public void destroy() {
        log.info("TraceIdFilter destroyed");
    }

    private String generateTraceId() {
        return System.currentTimeMillis() + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
