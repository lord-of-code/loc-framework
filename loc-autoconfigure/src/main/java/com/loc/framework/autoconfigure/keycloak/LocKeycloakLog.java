package com.loc.framework.autoconfigure.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class LocKeycloakLog {

  private final ObjectMapper objectMapper;

  LocKeycloakLog(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  void save(LocKeycloakLogDomain locKeycloakLogDomain) throws JsonProcessingException {
    log.info("security log: {}", objectMapper.writeValueAsString(locKeycloakLogDomain));
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  static class LocKeycloakLogDomain {

    private String userName;
    private String url;
    private String param;
    private LocalDateTime createDateTime;
    private String ip;
  }

}
