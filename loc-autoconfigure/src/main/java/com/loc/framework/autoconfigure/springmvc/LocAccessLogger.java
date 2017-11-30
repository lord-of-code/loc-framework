package com.loc.framework.autoconfigure.springmvc;

import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;

/**
 * Created on 2017/11/30.
 */
@Slf4j
public class LocAccessLogger {

  private LocSpringMvcProperties properties;

  private final static String REQUEST_PREFIX = " Request Info[ ";
  private final static String REQUEST_SUFFIX = " ] ";
  private final static String RESPONSE_PREFIX = " Response Info [ ";
  private final static String RESPONSE_SUFFIX = " ] ";

  private final static String DEFAULT_SKIP_PATTERN =
      "/api-docs.*|/autoconfig|/env|/configprops|/dump|/health|/info|/metrics.*|/mappings|/trace|/swagger.*|.*\\.png|.*\\.css|.*\\.js|.*\\.html|/favicon.ico|/hystrix.stream";

  private final static Pattern SKIP_PATTERNS = Pattern.compile(DEFAULT_SKIP_PATTERN);

  private StringBuilder normalMsg = new StringBuilder();

  public LocAccessLogger(LocSpringMvcProperties properties) {
    this.properties = properties;
  }

  public void appendRequestMessage(HttpServletRequest request) {
    normalMsg.append(REQUEST_PREFIX);
    normalMsg.append(normalRequestMessage(request));
    normalMsg.append(REQUEST_SUFFIX);
  }

  public void appendResponseMessage(HttpServletResponse response) {
    normalMsg.append(RESPONSE_PREFIX);
    normalMsg.append(normalResponseMessage(response));
  }

  private String normalResponseMessage(HttpServletResponse response) {
    return "";
  }

  public void appendTime(long time) {
    if(!properties.isIncludeResponse()) {
      normalMsg.append(RESPONSE_PREFIX);
    }
    normalMsg.append(";cost=").append(time);
    normalMsg.append(RESPONSE_SUFFIX);
  }

  private String normalRequestMessage(HttpServletRequest request) {
    StringBuilder msg = new StringBuilder();
    msg.append("uri=").append(request.getRequestURI());

    if(properties.isIncludeRequest()) {
      String queryString = request.getQueryString();
      if (queryString != null) {
        msg.append('?').append(queryString);
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
      msg.append(";headers=").append(new ServletServerHttpRequest(request).getHeaders());
    }

    if(!isNormalRequest(request)) {
      return msg.toString();
    }

    ContentCachingRequestWrapper wrapper =
        WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
    if (wrapper != null) {
      byte[] buf = wrapper.getContentAsByteArray();
      if (buf.length > 0) {
        int length = Math.min(buf.length, properties.getRequestBodyLength());
        String payload;
        try {
          payload = new String(buf, 0, length, wrapper.getCharacterEncoding());
        }
        catch (UnsupportedEncodingException ex) {
          payload = "[unknown]";
        }
        msg.append(";payload=").append(payload);
      }
    }
    return msg.toString();
  }

  public void printLog() {
    log.info(this.normalMsg.toString());
  }


  private boolean noContain(HttpServletRequest request) {
    String path = request.getServletPath();
    return !SKIP_PATTERNS.matcher(path).matches();
  }

  private boolean isNormalRequest(HttpServletRequest request) {
    return !isMultipart(request) && !isBinaryContent(request) && noContain(request);
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

}
