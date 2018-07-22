package com.loc.framework.autoconfigure.keycloak;

import com.google.common.collect.Sets;
import java.time.LocalDateTime;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.facade.SimpleHttpFacade;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class LocKeycloakLogInterceptor implements HandlerInterceptor {


  @Value("${keycloak.resource}")
  private String keycloakResource;

  private final LocKeycloakLog locKeycloakLog;

  LocKeycloakLogInterceptor(LocKeycloakLog locKeycloakLog) {
    this.locKeycloakLog = locKeycloakLog;
  }

  @Override
  public boolean preHandle(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, Object handler) {
    try {
      SimpleHttpFacade simpleHttpFacade = new SimpleHttpFacade(httpServletRequest,
          httpServletResponse);
      AccessToken accessToken = simpleHttpFacade.getSecurityContext().getToken();
      Set<String> resourceRoles = Sets.newHashSet();
      AccessToken.Access resourceAccess = accessToken.getResourceAccess()
          .getOrDefault(keycloakResource, null);
      if (resourceAccess != null) {
        resourceRoles = resourceAccess.getRoles();
      }
      locKeycloakLog.save(
          LocKeycloakLog.LocKeycloakLogDomain.builder()
              .param(httpServletRequest.getParameterMap().toString())
              .createDateTime(LocalDateTime.now()).url(httpServletRequest.getContextPath())
              .userName(accessToken.getName()).email(accessToken.getEmail())
              .realmRoles(accessToken.getRealmAccess().getRoles())
              .resourceRoles(resourceRoles).build());
      log.info("keycloak security pre handle {} ({}) in {} access {}", accessToken.getName(),
          accessToken.getEmail(), accessToken.getAudience()[0], httpServletRequest.getRequestURI());
    } catch (Exception e) {
      log.warn(e.getMessage(), e);
    }
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, Object handler, ModelAndView modelAndView) {
    log.info("keycloak security post handle status {}", httpServletResponse.getStatus());
  }
}
