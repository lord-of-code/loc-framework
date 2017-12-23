package com.loc.framework.logback.test;

import com.loc.framework.logback.sample.Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created on 2017/12/23.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class LogbackTest {

  @Test
  public void contextLoads() {
    log.info("测试服务器启动成功");
  }
}
