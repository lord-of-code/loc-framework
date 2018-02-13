package com.loc.framework.autoconfigure.test.springmvc;

import com.google.common.collect.Lists;
import com.loc.framework.autoconfigure.keycloak.LocKeycloakAutoConfiguration;
import com.loc.framework.autoconfigure.shutdown.TomcatGracefulShutdownAutoConfiguration;
import com.loc.framework.autoconfigure.springmvc.BasicResult;
import com.loc.framework.autoconfigure.springmvc.LocAdviceErrorAutoConfiguration;
import com.loc.framework.autoconfigure.springmvc.LocSpringMvcAutoConfiguration;
import com.loc.framework.autoconfigure.test.springmvc.LocKeycloakTest.KeycloakRequestController;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2018/1/29.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(value = KeycloakRequestController.class)
@TestPropertySource(properties = {
    "loc.tomcat.shutdown.waitTime = 5",
    "keycloak.enabled = true",
    "keycloak.realm = keycloak-loc",
    "keycloak.bearer-only = true",
    "keycloak.auth-server-utl = http://127.0.0.1:8080/auth",
    "keycloak.ssl-required = external",
    "keycloak.cors = true",
    "keycloak.resource = loc-app",
    "keycloak.use-resource-role-mappings = true",
    "keycloak.principal-attribute = preferred_username"
})

@DirtiesContext
public class LocKeycloakTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void getSuccess() throws Exception {
//    this.mockMvc
//        .perform(get("/keycloak/success").accept("application/json"))
//        .andExpect(jsonPath("$.code").value("200000"))
//        .andExpect(jsonPath("$.msg").value("success"))
//        .andExpect(jsonPath("$.detailMsg").value("success"))
//        .andExpect(jsonPath("$.data.name").value("thomas"))
//        .andExpect(jsonPath("$.data.age").value("29"))
//        .andExpect(status().isOk()).andReturn();
  }

  @MinimalWebConfiguration
  @RestController
  @Validated
  public static class KeycloakRequestController {

    @GetMapping(value = "/keycloak/success")
    public BasicResult<Demo> responseKeycloakSuccess() {
      Demo demo = new Demo();
      demo.setName("thomas");
      demo.setAge(30);
      demo.setAddress(Lists.newArrayList("a1", "a2"));
      return BasicResult.success(demo);
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Demo {

    @Size(min = 1, max = 10, message = "name长度范围是1～10")
    private String name;
    private int age;
    @NotNull(message = "address不能为空")
    private List<String> address;
  }

  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @Configuration
  @Import({
      ServletWebServerFactoryAutoConfiguration.class,
      JacksonAutoConfiguration.class,
      LocAdviceErrorAutoConfiguration.class,
      LocSpringMvcAutoConfiguration.class,
      TomcatGracefulShutdownAutoConfiguration.class,
//      KeycloakAutoConfiguration.class,
      LocKeycloakAutoConfiguration.class
  })
  protected @interface MinimalWebConfiguration {

  }
}
