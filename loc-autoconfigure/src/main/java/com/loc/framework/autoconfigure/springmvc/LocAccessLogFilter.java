package com.loc.framework.autoconfigure.springmvc;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;

/**
 * Created on 2017/11/30.
 */

@AllArgsConstructor
public class LocAccessLogFilter extends OncePerRequestFilter {

  private LocSpringMvcProperties properties;

  @Override
  protected void doFilterInternal(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, FilterChain filterChain)
      throws ServletException, IOException {

    boolean isFirstRequest = !isAsyncDispatch(httpServletRequest);
    HttpServletRequest requestToUse = httpServletRequest;

    if (properties
        .isIncludeRequestBody() && isFirstRequest && !(httpServletRequest instanceof ContentCachingRequestWrapper)) {
      requestToUse = new ContentCachingRequestWrapper(httpServletRequest,
          properties.getRequestBodyLength());
    }

    if ((properties.isIncludeRequestBody() || properties
        .isIncludeRequestHeaders()) && isFirstRequest) {
      beforeRequest(requestToUse, getBeforeMessage(requestToUse));
    }
    try {
      filterChain.doFilter(requestToUse, httpServletResponse);
    } finally {
      if ((properties.isIncludeResponseBody() || properties
          .isIncludeResponseHeaders()) && !isAsyncStarted(requestToUse)) {
        afterRequest(requestToUse, httpServletResponse, getAfterMessage(requestToUse, httpServletResponse));
      }
    }
  }

  private void beforeRequest(HttpServletRequest request, String message) {

  }

  private void afterRequest(HttpServletRequest request, HttpServletResponse response, String message) {

  }

  private String getBeforeMessage(HttpServletRequest request) {
    return "";
  }

  private String getAfterMessage(HttpServletRequest request, HttpServletResponse response) {
    return "";
  }

}
