package com.loc.framework.autoconfigure.springmvc;

import static com.loc.framework.autoconfigure.utils.LocConstants.BIND_ERROR_CODE;
import static com.loc.framework.autoconfigure.utils.LocConstants.CONSTRAINT_VIOLATION_ERROR_CODE;
import static com.loc.framework.autoconfigure.utils.LocConstants.METHOD_ARGUMENT_NOT_VALID_ERROR_CODE;

import com.loc.framework.autoconfigure.LocServiceException;
import com.loc.framework.autoconfigure.utils.ProblemUtil;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.apiguardian.api.API;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.general.GeneralAdviceTrait;
import org.zalando.problem.spring.web.advice.http.HttpAdviceTrait;
import org.zalando.problem.spring.web.advice.io.IOAdviceTrait;
import org.zalando.problem.spring.web.advice.routing.RoutingAdviceTrait;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

@API(status = API.Status.STABLE)
public interface LocServiceAdviceTrait extends GeneralAdviceTrait, HttpAdviceTrait, IOAdviceTrait,
    RoutingAdviceTrait, SecurityAdviceTrait {

  @API(status = API.Status.INTERNAL)
  @ExceptionHandler(value = LocServiceException.class)
  default ResponseEntity<Problem> handleHnServiceException(
      final LocServiceException hnServiceException, final NativeWebRequest request) {
    return this.create(hnServiceException,
        ProblemUtil.createProblem(hnServiceException.getMsg(), hnServiceException.getCode()),
        request);
  }

  @API(status = API.Status.INTERNAL)
  @ExceptionHandler(value = ConstraintViolationException.class)
  default ResponseEntity<Problem> handleConstraintViolationException(
      final ConstraintViolationException constraintViolationException,
      final NativeWebRequest request) {
    return this.create(constraintViolationException, ProblemUtil.createProblem(
        constraintViolationException.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage).collect(Collectors.joining(",")),
        CONSTRAINT_VIOLATION_ERROR_CODE), request);
  }

  @API(status = API.Status.INTERNAL)
  @ExceptionHandler(value = BindException.class)
  default ResponseEntity<Problem> handleBindException(final BindException bindException,
      final NativeWebRequest request) {
    List<FieldError> fieldErrors = bindException.getBindingResult().getFieldErrors();
    return this.create(bindException, ProblemUtil.createProblem(
        fieldErrors.stream().map(e -> e.getField() + ":" + e.getDefaultMessage())
            .collect(Collectors.joining(",")), BIND_ERROR_CODE), request);
  }

  @API(status = API.Status.INTERNAL)
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  default ResponseEntity<Problem> handleMethodArgumentNotValidException(
      final MethodArgumentNotValidException methodArgumentNotValidException,
      final NativeWebRequest request) {
    List<FieldError> fieldErrors = methodArgumentNotValidException.getBindingResult().getFieldErrors();
    return this.create(methodArgumentNotValidException, ProblemUtil.createProblem(
        fieldErrors.stream().map(e -> e.getField() + ":" + e.getDefaultMessage())
            .collect(Collectors.joining(",")), METHOD_ARGUMENT_NOT_VALID_ERROR_CODE), request);
  }
}

