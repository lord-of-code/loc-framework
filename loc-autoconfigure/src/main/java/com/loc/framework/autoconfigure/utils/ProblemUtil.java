package com.loc.framework.autoconfigure.utils;

import static com.loc.framework.autoconfigure.utils.LocConstants.SUCCESS_RESPONSE_CODE;

import org.slf4j.MDC;
import org.zalando.problem.Problem;

/**
 * Created on 2018/5/24.
 * TODO 返回结果的规定
 *
 * 如果状态码是2xx, 那么业务是正常的，然后看code字段，如果code字段为0，则根据data字段获取返回数据，如果code字段不为0，则通过detail展示业务异常
 * 如果状态码是4xx，那么传入的参数有问题，根据detail返回值进行展示
 * 如果状态码是5xx，那么服务端有异常，统一展示内部错误
 */
public interface ProblemUtil {

  static Problem createProblem(Object data) {
    String traceId = MDC.get("traceId");
    if (traceId != null) {
      return Problem.builder().withDetail("success").with("code", SUCCESS_RESPONSE_CODE)
          .with("data", data).with("traceId", traceId).build();
    } else {
      return Problem.builder().withDetail("success").with("code", SUCCESS_RESPONSE_CODE)
          .with("data", data).build();
    }
  }

  static Problem createProblem(String detail, Object data) {
    String traceId = MDC.get("traceId");
    if (traceId != null) {
      return Problem.builder().withDetail(detail).with("code", SUCCESS_RESPONSE_CODE)
          .with("data", data).with("traceId", traceId).build();
    } else {
      return Problem.builder().withDetail(detail).with("code", SUCCESS_RESPONSE_CODE)
          .with("data", data).build();
    }
  }

  static Problem createProblem(String detail, int code, Object data) {
    String traceId = MDC.get("traceId");
    if (traceId != null) {
      return Problem.builder().withDetail(detail).with("code", code).with("data", data)
          .with("traceId", traceId).build();
    } else {
      return Problem.builder().withDetail(detail).with("code", code).with("data", data).build();
    }
  }

  static Problem createProblem(String detail, int code) {
    String traceId = MDC.get("traceId");
    if (traceId != null) {
      return Problem.builder().withDetail(detail).with("code", code).with("traceId", traceId)
          .build();
    } else {
      return Problem.builder().withDetail(detail).with("code", code).build();
    }
  }
}
