package com.loc.framework.autoconfigure.keycloak;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

@Slf4j
public class LocKeycloakLogInterceptor implements WebRequestInterceptor {

  private final LocKeycloakLog locKeycloakLog;

  LocKeycloakLogInterceptor(LocKeycloakLog locKeycloakLog) {
    this.locKeycloakLog = locKeycloakLog;
  }

  @Override
  public void preHandle(WebRequest request) throws Exception {
    log.info("preHandle: request is {}", request);
  }

  @Override
  public void postHandle(WebRequest request, @Nullable ModelMap model) throws Exception {
    log.info("postHanlder: request is {}", request);
  }

  @Override
  public void afterCompletion(WebRequest request, @Nullable Exception ex) throws Exception {
    log.info("afterCompletion: request is {}", request);
    KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) SecurityContextHolder
        .getContext().getAuthentication().getPrincipal();
    locKeycloakLog.save(
        LocKeycloakLog.LocKeycloakLogDomain.builder().param(request.getParameterMap().toString())
            .createDateTime(LocalDateTime.now()).url(request.getContextPath())
            .userName(keycloakPrincipal.getName()).build());
  }
}
