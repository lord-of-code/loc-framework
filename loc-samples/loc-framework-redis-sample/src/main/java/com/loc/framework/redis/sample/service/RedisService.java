package com.loc.framework.redis.sample.service;

import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Created on 2017/8/10.
 */
@Service
public class RedisService {

  @Cacheable(value = "redis_demo_2", key = "'loc'")
  public RedisDemoInfo getDemoInfoLocKey(String name, int age) {
    return getRedisDemoInfo(name, age);
  }

  private RedisDemoInfo getRedisDemoInfo(String name, int age) {
    UserDemoInfo userDemoInfo = UserDemoInfo.builder().name("dragon").date(new Date()).build();
    List<UserDemoInfo> userDemoInfos = Lists.newArrayList(userDemoInfo);
    return RedisDemoInfo.builder().name(name).age(age).userDemoInfoList(userDemoInfos).build();
  }


  @Data
  @Builder
  public static class RedisDemoInfo {

    private String name;
    private int age;

    private List<UserDemoInfo> userDemoInfoList;
  }

  @Data
  @Builder
  static class UserDemoInfo {

    private Date date;
    private String name;
  }

}
