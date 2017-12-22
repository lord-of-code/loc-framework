package com.loc.framework.autoconfigure.test.springmvc;

import static org.assertj.core.api.Assertions.assertThat;

import com.loc.framework.autoconfigure.springmvc.BasicResult;
import com.loc.framework.autoconfigure.swagger.Swagger2AutoConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import javax.validation.Valid;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Created on 2017/12/17.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@TestPropertySource(properties = {
    "loc.web.springmvc.swagger2.apiinfo.title = 测试项目标题",
    "loc.web.springmvc.swagger2.apiinfo.description = 测试项目描述",
    "loc.web.springmvc.swagger2.apiinfo.termsOfServiceUrl = 测试项目url",
    "loc.web.springmvc.swagger2.apiinfo.contactName = 测试项目联系方式",
    "loc.web.springmvc.swagger2.apiinfo.license = 测试项目执照",
    "loc.web.springmvc.swagger2.restapi.basePackage = com.loc.framework.autoconfigure.test.springmvc"
})
@DirtiesContext
public class LocSwagger2Test {


  @Autowired
  private ApiInfo apiInfo;

  @Autowired
  private Docket docket;

  @Test
  public void testApiInfo() {
    assertThat(apiInfo.getTitle()).isEqualTo("测试项目标题");
    assertThat(apiInfo.getDescription()).isEqualTo("测试项目描述");
    assertThat(apiInfo.getLicense()).isEqualTo("测试项目执照");
    assertThat(apiInfo.getLicenseUrl()).isEqualTo("https://github.com/lord-of-code/loc-framework");
    assertThat(apiInfo.getTermsOfServiceUrl()).isEqualTo("测试项目url");
    assertThat(apiInfo.getVersion()).isEqualTo("v1");
    assertThat(apiInfo.getContact().getName()).isEqualTo("测试项目联系方式");
    assertThat(apiInfo.getContact().getEmail()).isEqualTo("dragonlong1986@126.com");
    assertThat(apiInfo.getContact().getUrl())
        .isEqualTo("https://github.com/lord-of-code/loc-framework");
  }

  @Test
  public void testRestApi() {
    assertThat(docket.getDocumentationType()).isEqualTo(DocumentationType.SWAGGER_2);
  }

  @MinimalWebConfiguration
  @RestController
  @Validated
  public static class Swagger2Controller {


    @GetMapping(value = "/swagger/get")
    public BasicResult<Demo> swaggerGet() {
      return BasicResult.fail(200_001, "显示的错误", "详细的错误");
    }

    @PostMapping(value = "/swagger/post1")
    public BasicResult<Demo> swaggerPost1(
        @RequestParam @Size(min = 1, max = 10, message = "字符串长度在1~10之间") String name,
        @NotNull(message = "age不能为空") @RequestParam int age,
        @NotNull(message = "address不能为空") @Size(min = 1, max = 3, message = "数组长度范围在1～3之间") @RequestParam(required = false) List<String> address) {
      Demo demo = new Demo();
      demo.setName(name);
      demo.setAge(age);
      demo.setAddress(address);
      return BasicResult.success(demo);
    }

    @PostMapping(value = "/swagger/post2")
    public BasicResult<Demo> swaggerPost2(
        @Valid @RequestBody Demo demo) {
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
      Swagger2AutoConfiguration.class
  })
  protected @interface MinimalWebConfiguration {

  }
}
