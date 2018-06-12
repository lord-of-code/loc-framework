package com.loc.framework.autoconfigure.feign;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;

/**
 * Created on 2018/5/27.
 */
public class LocFeignEncoderConfig {

  @Autowired
  private ObjectFactory<HttpMessageConverters> messageConverters;

  @Bean
  public Encoder feignFormEncoder() {
    return new SpringFormEncoder();
  }

}
