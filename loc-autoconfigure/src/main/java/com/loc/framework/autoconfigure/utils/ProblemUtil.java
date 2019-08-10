package com.loc.framework.autoconfigure.utils;

import org.slf4j.MDC;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

public interface ProblemUtil {

  static Problem createProblem(String detail, int code, Object data) {
    String traceId = MDC.get("traceId");
    if (traceId != null) {
      return Problem.builder().withDetail(detail).with("code", code).with("data", data)
          .with("traceId", traceId).withStatus(Status.OK).build();
    } else {
      return Problem.builder().withDetail(detail).with("code", code).with("data", data)
          .withStatus(Status.OK).build();
    }
  }

  static Problem createProblem(String detail, int code) {
    String traceId = MDC.get("traceId");
    if (traceId != null) {
      return Problem.builder().withDetail(detail).with("code", code).with("traceId", traceId)
          .withStatus(Status.OK).build();
    } else {
      return Problem.builder().withDetail(detail).with("code", code).withStatus(Status.OK).build();
    }
  }

  static Problem createProblem(String detail, int code, Status status) {
    String traceId = MDC.get("traceId");
    if (traceId != null) {
      return Problem.builder().withDetail(detail).with("code", code).with("traceId", traceId)
          .withStatus(status).build();
    } else {
      return Problem.builder().withDetail(detail).with("code", code).withStatus(status).build();
    }
  }
}
