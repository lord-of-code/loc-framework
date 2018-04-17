package com.loc.framework.cloud.client.sample.feign;

import com.loc.framework.autoconfigure.springmvc.BasicResult;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created on 2018/4/17.
 */
@Component
@Slf4j
public class FeignClientSampleFallBackFactory implements FallbackFactory<FeignClientSample> {

  @Override
  public FeignClientSample create(Throwable throwable) {
    return () -> {
      log.error("错误：{}", throwable.getMessage(), throwable);
      return BasicResult.fail(500_100, "请求失败");
    };
  }
}
