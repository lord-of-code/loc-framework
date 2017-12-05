package com.loc.framework.autoconfigure.springmvc;

import com.google.common.collect.Lists;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

/**
 * Created on 2017/11/30.
 */
@Slf4j
public class LocAccessLogger {

  @Getter
  private LocSpringMvcProperties properties;

  private static final String REQUEST_PREFIX = "Request Info [";
  private static final String REQUEST_SUFFIX = "] ";
  private static final String RESPONSE_PREFIX = "Response Info [";
  private static final String RESPONSE_SUFFIX = "] ";


  private StringBuilder normalMsg = new StringBuilder();

  LocAccessLogger(LocSpringMvcProperties properties) {
    this.properties = properties;
  }

  void appendRequestCommonMessage(HttpServletRequest request) {
    normalMsg.append(REQUEST_PREFIX);
    StringBuilder msg = new StringBuilder();
    msg.append("uri=").append(request.getRequestURI());
    msg.append(";method=").append(request.getMethod());

    if (properties.isIncludeRequest()) {
      String queryString = request.getQueryString();
      if (queryString != null) {
        msg.append('?').append(queryString);
      }

      Map<String, String[]> params = request.getParameterMap();
      if (params != null && !params.isEmpty()) {
        msg.append(";params={");
        msg.append(getParams(params));
        msg.append("}");
      }

      String client = request.getRemoteAddr();
      if (StringUtils.hasLength(client)) {
        msg.append(";client=").append(client);
      }
      HttpSession session = request.getSession(false);
      if (session != null) {
        msg.append(";session=").append(session.getId());
      }
      String user = request.getRemoteUser();
      if (user != null) {
        msg.append(";user=").append(user);
      }
    }
    normalMsg.append(msg.toString());
  }

  void appendRequestDetailMessage(boolean includeRequest, HttpServletRequest request) {
    StringBuilder msg = new StringBuilder();
    if (includeRequest && isNormalRequest(request)) {
      msg.append(";headers=").append(new ServletServerHttpRequest(request).getHeaders());
      ContentCachingRequestWrapper wrapper = WebUtils
          .getNativeRequest(request, ContentCachingRequestWrapper.class);
      if (wrapper != null) {
        byte[] buf = wrapper.getContentAsByteArray();
        if (buf.length > 0) {
          int length = Math.min(buf.length, properties.getRequestBodyLength());
          String payload;
          try {
            payload = new String(buf, 0, length, wrapper.getCharacterEncoding());
          } catch (UnsupportedEncodingException ex) {
            payload = "[unknown]";
          }
          msg.append(";payload=").append(payload);
        }
      }
    }
    normalMsg.append(msg.toString());
    normalMsg.append(REQUEST_SUFFIX);
  }

  void appendResponseCommonMessage(ContentCachingResponseWrapper response, long cost) {
    normalMsg.append(RESPONSE_PREFIX);
    StringBuilder msg = new StringBuilder();
    msg.append("status=").append(response.getStatusCode());
    msg.append(";size=").append(response.getContentSize());
    msg.append(";cost=").append(cost);
    normalMsg.append(msg.toString());
  }

  void appendResponseDetailMessage(ContentCachingResponseWrapper response) {
    StringBuilder msg = new StringBuilder();
    String contentType = response.getContentType();
    msg.append(";headers=").append(new ServletServerHttpResponse(response).getHeaders());
    Optional.ofNullable(contentType).filter(c -> c.startsWith("application/json")).ifPresent(c -> {
      byte[] buf = response.getContentAsByteArray();
      if (buf.length > 0) {
        int length = Math.min(buf.length, properties.getResponseBodyLength());
        String payload;
        try {
          payload = new String(buf, 0, length, response.getCharacterEncoding());
        } catch (UnsupportedEncodingException ex) {
          payload = "[unknown]";
        }
        msg.append(";payload=").append(payload);
      }
    });
    normalMsg.append(msg.toString());
  }

  void appendResponseLast() {
    normalMsg.append(RESPONSE_SUFFIX);
  }

  void printLog() {
    log.info(this.normalMsg.toString());
  }

  private boolean isNormalRequest(HttpServletRequest request) {
    return !isMultipart(request) && !isBinaryContent(request);
  }

  private boolean isMultipart(final HttpServletRequest request) {
    return request.getContentType() != null && request.getContentType()
        .startsWith("multipart/form-data");
  }

  private boolean isBinaryContent(final HttpServletRequest request) {
    if (request.getContentType() == null) {
      return false;
    }
    return request.getContentType().startsWith("image") || request.getContentType()
        .startsWith("video") || request.getContentType().startsWith("audio");
  }

  private String getParams(final Map<String, String[]> params) {
    List<String> parts = Lists.newArrayList();
    params.forEach((k, v) -> {
      String param = k + "=[" + Arrays.stream(v).map(String::valueOf)
          .collect(Collectors.joining(",")) + "]";
      parts.add(param);
    });
    return parts.stream().collect(Collectors.joining(","));
  }

}
