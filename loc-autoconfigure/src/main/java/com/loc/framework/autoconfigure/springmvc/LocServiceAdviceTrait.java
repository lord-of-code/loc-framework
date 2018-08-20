package com.loc.framework.autoconfigure.springmvc;

import com.loc.framework.autoconfigure.LocServiceException;
import com.loc.framework.autoconfigure.utils.ProblemUtil;
import org.apiguardian.api.API;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@API(status = API.Status.STABLE)
public interface LocServiceAdviceTrait extends ProblemHandling {

  @API(status = API.Status.INTERNAL)
  @ExceptionHandler(value = LocServiceException.class)
  default ResponseEntity<Problem> handleProblem(final LocServiceException hnServiceException,
      final NativeWebRequest request) {
    return this.create(hnServiceException,
        ProblemUtil.createProblem(hnServiceException.getMsg(), hnServiceException.getCode()), request);
  }
}

