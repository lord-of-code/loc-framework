package com.loc.framework.mybatis.xml.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;

/**
 * Created on 2018/1/15.
 */
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class
})
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
