package com.loc.framework.cloud.client.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Created on 2017/12/2.
 */
@SpringCloudApplication
@EnableFeignClients
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
