package com.fya.creditos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RateLimitFilterTest {

    private static final int REQUESTS_PER_MINUTE = 20;

    private RateLimitFilter filter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new RateLimitFilter();
        filterChain = mock(FilterChain.class);
    }

    @Test
    void allowsUpToTheLimitThenBlocksTheSameIp() throws Exception {
        for (int i = 0; i < REQUESTS_PER_MINUTE; i++) {
            HttpServletRequest request = requestFromIp("203.0.113.10");
            HttpServletResponse response = mock(HttpServletResponse.class);

            filter.doFilterInternal(request, response, filterChain);

            verify(response, never()).setStatus(429);
        }
        verify(filterChain, times(REQUESTS_PER_MINUTE)).doFilter(any(), any());

        HttpServletRequest oneTooMany = requestFromIp("203.0.113.10");
        StringWriter body = new StringWriter();
        HttpServletResponse blockedResponse = mock(HttpServletResponse.class);
        when(blockedResponse.getWriter()).thenReturn(new PrintWriter(body));

        filter.doFilterInternal(oneTooMany, blockedResponse, filterChain);

        verify(blockedResponse).setStatus(429);
        verify(filterChain, times(REQUESTS_PER_MINUTE)).doFilter(any(), any());
        assertThat(body.toString()).contains("Too many requests");
    }

    @Test
    void tracksBucketsIndependentlyPerIp() throws Exception {
        for (int i = 0; i < REQUESTS_PER_MINUTE; i++) {
            filter.doFilterInternal(requestFromIp("203.0.113.10"), mock(HttpServletResponse.class), filterChain);
        }

        // A different IP should still have its own full bucket.
        HttpServletResponse otherIpResponse = mock(HttpServletResponse.class);
        filter.doFilterInternal(requestFromIp("198.51.100.20"), otherIpResponse, filterChain);

        verify(otherIpResponse, never()).setStatus(429);
        verify(filterChain, times(REQUESTS_PER_MINUTE + 1)).doFilter(any(), any());
    }

    @Test
    void prefersXForwardedForOverRemoteAddrBehindAProxy() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("198.51.100.55, 10.0.0.1");
        when(request.getRemoteAddr()).thenReturn("10.0.0.1"); // the proxy, same for every client

        for (int i = 0; i < REQUESTS_PER_MINUTE; i++) {
            filter.doFilterInternal(request, mock(HttpServletResponse.class), filterChain);
        }

        HttpServletResponse blockedResponse = mock(HttpServletResponse.class);
        when(blockedResponse.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        filter.doFilterInternal(request, blockedResponse, filterChain);

        verify(blockedResponse).setStatus(429);
    }

    private HttpServletRequest requestFromIp(String ip) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(ip);
        return request;
    }
}
