package com.loc.framework.autoconfigure.springmvc;

import com.google.common.base.Strings;
import com.loc.framework.autoconfigure.common.BaseResult;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Configuration
@Slf4j
@RestControllerAdvice
@ConditionalOnClass(org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice.class)
@ConditionalOnWebApplication
public class LocResponseBodyAdvice implements
    org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice {


  @Override
  public boolean supports(MethodParameter methodParameter, Class aClass) {
    Method method = methodParameter.getMethod();
    if (method == null) {
      return false;
    }

    Class<?> clazz = method.getReturnType();
    return clazz != null && BaseResult.class.isAssignableFrom(clazz);

  }

  @Nullable
  @Override
  public Object beforeBodyWrite(@Nullable Object returnValue, MethodParameter methodParameter,
      MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest,
      ServerHttpResponse serverHttpResponse) {
    BaseResult<?> response=(BaseResult) returnValue;
    String traceId = MDC.get("traceId");
    if(response != null && !Strings.isNullOrEmpty(traceId)) {
      response.setTraceId(traceId);
    }
    return response;
  }
}
