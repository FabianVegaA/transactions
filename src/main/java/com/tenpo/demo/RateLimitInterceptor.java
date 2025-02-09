package com.tenpo.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final String HEADER_API_KEY = "X-api-key";
    private static final String HEADER_LIMIT_REMAINING = "X-Rate-Limit-Remaining";
    private static final String HEADER_RETRY_AFTER = "X-Rate-Limit-Retry-After-Seconds";

    @Autowired
    TenpoService tenpoService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        String apiKey = request.getHeader(HEADER_API_KEY);
        if (apiKey == null || apiKey.isEmpty()) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Missing Header: " + HEADER_API_KEY);
            return false;
        }

        Bucket bucket = tenpoService.resolveBucket(apiKey);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            response.addHeader(HEADER_LIMIT_REMAINING, String.valueOf(probe.getRemainingTokens()));
            return true;
        }
        long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.addHeader(HEADER_RETRY_AFTER, String.valueOf(waitForRefill));
        response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "You have exhausted your API Request Quota");
        return false;
    }
}
