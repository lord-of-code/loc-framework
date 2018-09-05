package com.loc.framework.autoconfigure.okhttp;

import static com.loc.framework.autoconfigure.utils.LocConstants.OKHTTP_ERROR_CODE;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.loc.framework.autoconfigure.LocServiceException;
import com.loc.framework.autoconfigure.utils.ProblemUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

/**
 * Created on 2018/9/5.
 */
@Slf4j
public class LocOkHttpClient {

  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  @Getter
  private OkHttpClient okHttpClient;

  private ObjectMapper objectMapper;

  public LocOkHttpClient(OkHttpClient okHttpClient, ObjectMapper objectMapper) {
    this.okHttpClient = okHttpClient;
    this.objectMapper = objectMapper;
  }


  public Problem get(OkHttpClientBuilder hnGetBuilder) {
    checkBasicParam(hnGetBuilder);
    Builder builder = createRequestBuilder(
        createGetUrl(hnGetBuilder.getUrl(), hnGetBuilder.getParams()), hnGetBuilder.getHeaders());
    return returnBaseResult(builder.get().build(), hnGetBuilder.getTypeReference());
  }


  public Problem post(OkHttpClientBuilder okHttpClientBuilder) {
    checkBasicParam(okHttpClientBuilder);
    Builder builder = createRequestBuilder(okHttpClientBuilder.getUrl(),
        okHttpClientBuilder.getHeaders());

    FormBody.Builder formBuilder = new FormBody.Builder();
    Optional.ofNullable(okHttpClientBuilder.getParams()).ifPresent(c -> c.forEach(
        (key, value) -> {
          if (value != null) {
            formBuilder.add(key, value);
          } else {
            log.warn("key:{} is null", key);
          }
        }
    ));

    return returnBaseResult(builder.post(formBuilder.build()).build(),
        okHttpClientBuilder.getTypeReference()
    );
  }

  public Problem postEncoded(OkHttpClientBuilder hnPostBuilder, List<String> excludeEncode) {
    checkBasicParam(hnPostBuilder);
    Builder builder = createRequestBuilder(hnPostBuilder.getUrl(), hnPostBuilder.getHeaders());

    FormBody.Builder formBuilder = new FormBody.Builder();
    Optional.ofNullable(hnPostBuilder.getParams()).ifPresent(c -> c.forEach(
        (key, value) -> {
          if (value != null) {
            if (!excludeEncode.contains(key)) {
              formBuilder.add(key, value);
            } else {
              formBuilder.addEncoded(key, value);
            }
          } else {
            log.warn("key:{} is null", key);
          }
        }
    ));

    return returnBaseResult(builder.post(formBuilder.build()).build(),
        hnPostBuilder.getTypeReference()
    );
  }

  public Problem postJson(OkHttpClientBuilder okHttpClientBuilder) {
    checkBasicParam(okHttpClientBuilder);
    Builder builder = createRequestBuilder(okHttpClientBuilder.getUrl(),
        okHttpClientBuilder.getHeaders());
    checkBody(okHttpClientBuilder.getBody());
    RequestBody body = RequestBody.create(JSON, okHttpClientBuilder.getBody());

    return returnBaseResult(builder.post(body)
        .build(), okHttpClientBuilder.getTypeReference());
  }

  @SuppressWarnings("unchecked")
  private Problem returnBaseResult(Request request, TypeReference typeReference) {
    try {
      Response response = this.okHttpClient.newCall(request).execute();
      if (response.isSuccessful()) {
        String result = response.body().string();
        checkResult(result);
        Type type = typeReference.getType();
        if (isPrimitiveType(type)) {
          return ProblemUtil.createProblem(result);
        }

        return ProblemUtil.createProblem(objectMapper.readValue(result, typeReference));
      } else {
        log.error("request url: {} http status code:{}", request.url(), response.code());
        String result = Optional.ofNullable(response.body())
            .map(body -> {
              try {
                return Strings.emptyToNull(body.string());
              } catch (IOException e) {
                log.error("获取body失败:", e);
                return null;
              }
            })
            .orElse(Status.BAD_REQUEST.getReasonPhrase());
        return Problem.valueOf(Status.BAD_REQUEST, result, URI.create(request.url().toString()));
      }
    } catch (Exception e) {
      throw new LocServiceException(OKHTTP_ERROR_CODE, e.getMessage());
    }
  }


  private boolean isPrimitiveType(Type type) {
    if (type instanceof Class) {
      Class clazz = (Class) type;
      if (ClassUtils.isPrimitiveOrWrapper(clazz) || ClassUtils
          .isAssignable(clazz, String.class)) {
        return true;
      }
    }
    return false;
  }

  private void checkBody(String body) {
    if (StringUtils.isBlank(body)) {
      throw new RuntimeException("response 不能为空!");
    }
  }


  private void checkResult(String result) {
    if (StringUtils.isBlank(result)) {
      throw new RuntimeException("返回值为空!");
    }
  }

  private Builder createRequestBuilder(String url, Map<String, String> headers) {
    Builder builder = new Request.Builder();
    builder.url(url);
    Optional.ofNullable(headers).ifPresent(c -> c.forEach(builder::addHeader));
    return builder;
  }

  private void checkBasicParam(OkHttpClientBuilder hnOkHttpBuilder) {
    if (StringUtils.isBlank(hnOkHttpBuilder.getUrl())) {
      throw new RuntimeException("不能为空!");
    }

    if (hnOkHttpBuilder.getTypeReference() == null) {
      throw new RuntimeException("typeReference!");
    }
  }

  private String createGetUrl(String url, Map<String, String> params) {
    if (CollectionUtils.isEmpty(params)) {
      return url;
    }
    StringBuilder sb = new StringBuilder();
    params.forEach((k, v) -> {
      if (sb.length() == 0) {
        sb.append("?");
      } else if (sb.length() > 0) {
        sb.append("&");
      }
      sb.append(k);
      sb.append("=").append(v);
    });
    return url + sb.toString();
  }

}
