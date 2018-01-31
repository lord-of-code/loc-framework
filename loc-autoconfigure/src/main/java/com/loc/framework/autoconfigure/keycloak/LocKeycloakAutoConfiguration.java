package com.loc.framework.autoconfigure.keycloak;

import com.google.common.base.Strings;
import com.loc.framework.autoconfigure.springmvc.BasicResultCode;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationEntryPoint;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakPreAuthActionsFilter;
import org.keycloak.representations.adapters.config.BaseRealmConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Created on 2018/1/27.
 */
@ConditionalOnWebApplication
@ConditionalOnProperty(value = "keycloak.enabled", matchIfMissing = true)
@ConditionalOnClass({KeycloakWebSecurityConfigurerAdapter.class, BaseRealmConfig.class})
@KeycloakConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class LocKeycloakAutoConfiguration extends KeycloakWebSecurityConfigurerAdapter {

  private static final String NOT_AUTHENTICATION_MSG =
      "{\"code\":" + BasicResultCode.SECURITY_AUTHENTICATION.getCode() + ",\"msg\":\"" +
          BasicResultCode.SECURITY_AUTHENTICATION.getMsg() + "\"}";

  private static final String NOT_ACTIVE_TOKEN_MSG =
      "{\"code\":" + BasicResultCode.SECURITY_TOKEN_NOT_ACTIVE.getCode() + ",\"msg\":\"" +
          BasicResultCode.SECURITY_TOKEN_NOT_ACTIVE.getMsg() + "\"}";

  private final KeycloakClientRequestFactory keycloakClientRequestFactory;

  private final MethodSecurityMetadataSource methodSecurityMetadataSource;

  private final RequestMappingHandlerMapping requestMappingHandlerMapping;

  public LocKeycloakAutoConfiguration(
      KeycloakClientRequestFactory keycloakClientRequestFactory,
      MethodSecurityMetadataSource methodSecurityMetadataSource,
      RequestMappingHandlerMapping requestMappingHandlerMapping) {
    this.keycloakClientRequestFactory = keycloakClientRequestFactory;
    this.methodSecurityMetadataSource = methodSecurityMetadataSource;
    this.requestMappingHandlerMapping = requestMappingHandlerMapping;
  }


  @ConditionalOnClass(WebMvcConfigurer.class)
  @Import(LocKeycloakMvcConfiguration.class)
  @Configuration
  public static class LocKeycloakWvcConfiguration {

  }

  @Bean(name = "keycloakRestTemplate")
  public KeycloakRestTemplate keycloakRestTemplate() {
    return new KeycloakRestTemplate(keycloakClientRequestFactory);
  }

  @Bean
  @Override
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return new NullAuthenticatedSessionStrategy();
  }

  @Override
  protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(keycloakAuthenticationProvider());
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .sessionAuthenticationStrategy(sessionAuthenticationStrategy()).and()
        .addFilterBefore(keycloakPreAuthActionsFilter(), LogoutFilter.class)
        .addFilterBefore(keycloakAuthenticationProcessingFilter(), X509AuthenticationFilter.class)
        .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint()).and()
        .authorizeRequests()
        .requestMatchers(CorsUtils::isCorsRequest).permitAll()
        .antMatchers(urlPath()).authenticated() //允许被登录用户访问
        .anyRequest().permitAll();
  }

//  @Override
//  @Bean
//  protected AuthenticationEntryPoint authenticationEntryPoint() throws Exception {
//    return new LocKeycloakAuthenticationEntryPoint(this.adapterDeploymentContext());
//  }

  //用于把相关的keycloak的配置文件放到application.yml中
  @Bean
  KeycloakConfigResolver keycloakConfigResolver() {
    return new KeycloakSpringBootConfigResolver();
  }

  private String[] urlPath() {
    Set<String> antMatcherSet = new HashSet<>();
    Map<RequestMappingInfo, HandlerMethod> map = this.requestMappingHandlerMapping
        .getHandlerMethods();
    map.forEach((requestMappingInfo, handlerMethod) -> {
      Collection<ConfigAttribute> collection = methodSecurityMetadataSource
          .getAttributes(handlerMethod.getMethod(), handlerMethod.getBeanType());
      if (!CollectionUtils.isEmpty(collection)) {
        antMatcherSet.addAll(requestMappingInfo.getPatternsCondition().getPatterns());
      }
    });
    return antMatcherSet.toArray(new String[]{});
  }

  /**
   * 普通的不带bearer头的登录验证，提示未登录
   */
//  public class LocKeycloakAuthenticationEntryPoint extends KeycloakAuthenticationEntryPoint {
//
//    public LocKeycloakAuthenticationEntryPoint(
//        AdapterDeploymentContext adapterDeploymentContext) {
//      super(adapterDeploymentContext);
//    }
//
//    @Override
//    protected void commenceUnauthorizedResponse(HttpServletRequest request,
//        HttpServletResponse response) throws IOException {
//      write(response);
//    }
//  }
//
//
//  private void write(HttpServletResponse response) throws IOException {
//
//    response.setContentType("application/json; charset=utf-8");
//    String str = response.getHeader("WWW-Authenticate");
//
//    String result = Optional.ofNullable(Strings.emptyToNull(str))
//        .filter(s -> s.contains("invalid_token"))
//        .map(s -> NOT_ACTIVE_TOKEN_MSG).orElse(NOT_AUTHENTICATION_MSG);
//
//    try (PrintWriter out = response.getWriter()) {
//      out.write(result);
//    }
//  }
}
