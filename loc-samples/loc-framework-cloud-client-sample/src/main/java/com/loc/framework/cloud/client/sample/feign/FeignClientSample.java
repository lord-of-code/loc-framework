package com.loc.framework.cloud.client.sample.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.zalando.problem.Problem;


/**
 * Created on 2018/4/17.
 */
@FeignClient(name = "cloud-server-sample", fallbackFactory = FeignClientSampleFallBackFactory.class)
public interface FeignClientSample {

  @RequestMapping(value = "/server", method = RequestMethod.GET)
  Problem server();
}
