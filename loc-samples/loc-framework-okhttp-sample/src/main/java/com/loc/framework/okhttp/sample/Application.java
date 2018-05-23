package com.loc.framework.okhttp.sample;

import com.google.common.base.Strings;
import java.io.IOException;
import java.util.Optional;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

/**
 * Created on 2017/12/29.
 */
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Slf4j
  @RestController
  public static class OkHttpController {

    @Resource
    private OkHttpClient okHttpClient;

    @GetMapping(value = "/baidu")
    public Problem getBaidu() {
      Request request = new Request.Builder().url("https://www.baidu.com")
          .get().build();
      try {
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
          log.info(response.body().string());
        } else {
          log.info("response code is " + response.code());
          String result = Optional.ofNullable(response.body())
              .map(body -> {
                try {
                  return Strings.emptyToNull(body.string());
                } catch (IOException e) {
                  log.error("获取body失败:", e);
                  return null;
                }
              })
              .orElse("body is null");
          log.warn("response fail result is {}", result);
        }
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
      return return Problem.builder().with("data", "success").build();;
    }
  }

}
