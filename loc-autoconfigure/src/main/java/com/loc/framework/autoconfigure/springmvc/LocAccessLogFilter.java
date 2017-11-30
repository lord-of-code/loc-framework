package com.loc.framework.autoconfigure.springmvc;

import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Created on 2017/11/30.
 */

@Slf4j
@AllArgsConstructor
public class LocAccessLogFilter extends OncePerRequestFilter {

  private LocSpringMvcProperties properties;

  @Override
  protected void doFilterInternal(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, FilterChain filterChain)
      throws ServletException, IOException {

    final boolean isFirstRequest = !isAsyncDispatch(httpServletRequest);
    final LocAccessLogger accessLogger = new LocAccessLogger(this.properties);
    HttpServletRequest requestToUse = httpServletRequest;

    StopWatch watch = new StopWatch();
    watch.start();
    if (properties.isIncludeRequest() && isFirstRequest &&
        !(httpServletRequest instanceof ContentCachingRequestWrapper)) {
      requestToUse = new ContentCachingRequestWrapper(httpServletRequest,
          properties.getRequestBodyLength());
    }

    if (properties.isIncludeRequest() && isFirstRequest) {
      accessLogger.appendRequestMessage(requestToUse);
    }
    try {
      filterChain.doFilter(requestToUse, httpServletResponse);
    } finally {
      if (properties.isIncludeResponse() && !isAsyncStarted(requestToUse)) {
        accessLogger.appendResponseMessage(httpServletResponse);
      }
      accessLogger.appendTime(watch.getTotalTimeMillis());
      accessLogger.printLog();
    }
  }
}
