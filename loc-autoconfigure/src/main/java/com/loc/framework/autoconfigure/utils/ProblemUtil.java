package com.loc.framework.autoconfigure.utils;

import org.zalando.problem.Problem;

/**
 * Created on 2018/5/24.
 */
public interface ProblemUtil {

  static Problem createProblem(Object data) {
    return Problem.builder().withDetail("success").with("code", 0).with("data", data).build();
  }

  static Problem createProblem(String detail, Object data) {
    return Problem.builder().withDetail(detail).with("code", 0).with("data", data).build();
  }

  static Problem createProblem(String detail, int code, Object data) {
    return Problem.builder().withDetail(detail).with("code", code).with("data", data).build();
  }
}
