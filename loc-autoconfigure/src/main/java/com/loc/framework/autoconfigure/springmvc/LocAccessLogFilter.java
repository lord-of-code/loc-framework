package com.loc.framework.autoconfigure.springmvc;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;


/**
 * Created on 2017/11/30.
 */

@Slf4j
@AllArgsConstructor
public class LocAccessLogFilter extends OncePerRequestFilter {

  private LocSpringMvcProperties properties;

  private static final String DEFAULT_SKIP_PATTERN =
      "/api-docs.*|/actuator.*|/swagger.*|.*\\.png|.*\\.css|.*\\.js|.*\\.html|/favicon.ico|/hystrix.stream";

  private static final Pattern SKIP_PATTERNS = Pattern.compile(DEFAULT_SKIP_PATTERN);

  @Override
  protected void doFilterInternal(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, FilterChain filterChain)
      throws ServletException, IOException {
    if (ignoreRequest(httpServletRequest)) {
      filterChain.doFilter(httpServletRequest, httpServletResponse);
    } else {
      final boolean isFirstRequest = !isAsyncDispatch(httpServletRequest);
      final LocAccessLogger accessLogger = new LocAccessLogger(this.properties);
      HttpServletRequest requestToUse = httpServletRequest;
      ContentCachingResponseWrapper responseToUse =
          new ContentCachingResponseWrapper(httpServletResponse);

      StopWatch watch = new StopWatch();
      watch.start();
      if (properties.isIncludeRequest() && isFirstRequest
          && !(httpServletRequest instanceof ContentCachingRequestWrapper)) {
        requestToUse = new ContentCachingRequestWrapper(httpServletRequest,
            properties.getRequestBodyLength());
      }

      if (properties.isIncludeRequest() && isFirstRequest) {
        accessLogger.appendRequestMessage(requestToUse);
      }
      try {
        filterChain.doFilter(requestToUse, responseToUse);
      } finally {
        boolean hasResponse = false;
        if (properties.isIncludeResponse() && !isAsyncStarted(requestToUse) && !isBinaryContent(
            httpServletResponse) && !isMultipart(httpServletResponse)) {
          accessLogger.appendResponseMessage(responseToUse);
          hasResponse = true;
        }
        watch.stop();
        accessLogger.appendTime(hasResponse, watch.getTotalTimeMillis());

        responseToUse.copyBodyToResponse();
        accessLogger.printLog();
      }
    }
  }

  private boolean ignoreRequest(HttpServletRequest request) {
    String path = request.getRequestURI();
    return SKIP_PATTERNS.matcher(path).matches();
  }

  private boolean isBinaryContent(final HttpServletResponse response) {
    return response.getContentType() != null && (response.getContentType()
        .startsWith("image") || response.getContentType().startsWith("video") || response
        .getContentType().startsWith("audio"));
  }

  private boolean isMultipart(final HttpServletResponse response) {
    return response.getContentType() != null && (response.getContentType()
        .startsWith("multipart/form-data") || response.getContentType()
        .startsWith("application/octet-stream"));
  }
}
