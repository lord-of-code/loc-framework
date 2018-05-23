package com.loc.framework.cloud.client.sample.feign;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

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
      return Problem.valueOf(Status.BAD_REQUEST);
    };
  }
}
