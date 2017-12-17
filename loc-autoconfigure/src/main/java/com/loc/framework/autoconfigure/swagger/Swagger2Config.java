package com.loc.framework.autoconfigure.swagger;

import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@Configuration
@ConditionalOnClass(Docket.class)
@ConditionalOnProperty(value = "loc.web.springmvc.swagger2.enabled", matchIfMissing = true)
@EnableConfigurationProperties({Swagger2RestApiProperties.class, Swagger2ApiInfoProperties.class})
public class Swagger2Config {

  @Bean
  public Docket createRestApi(Swagger2RestApiProperties swaggerProperties, ApiInfo apiInfo) {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo)
        .select()
        .apis(Optional.ofNullable(swaggerProperties.getBasePackage())
            .map(RequestHandlerSelectors::basePackage).orElse(RequestHandlerSelectors.any()))
        .paths(Optional.ofNullable(swaggerProperties.getPaths()).map(PathSelectors::regex)
            .orElse(PathSelectors.any()))
        .build();
  }

  @Bean(name = "swagger2ApiInfo")
  public ApiInfo apiInfo(Swagger2ApiInfoProperties swagger2ApiInfoProperties) {
    return new ApiInfoBuilder()
        .title(swagger2ApiInfoProperties.getTitle()) // 标题
        .description(swagger2ApiInfoProperties.getDescription()) // 描述
        .termsOfServiceUrl(swagger2ApiInfoProperties.getTermsOfServiceUrl()) //网址
        .version(swagger2ApiInfoProperties.getVersion()) // 版本号
        .contact(new Contact(swagger2ApiInfoProperties.getContactName(),
            swagger2ApiInfoProperties.getContactUrl(), swagger2ApiInfoProperties.getContactEmail()))
        .license(swagger2ApiInfoProperties.getLicense())
        .licenseUrl(swagger2ApiInfoProperties.getLicenseUrl())
        .build();
  }


}
