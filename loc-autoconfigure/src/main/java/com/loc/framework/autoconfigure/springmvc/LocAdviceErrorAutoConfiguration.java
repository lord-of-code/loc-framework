package com.loc.framework.autoconfigure.springmvc;

import io.lettuce.core.RedisException;
import java.sql.SQLException;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Created on 2017/12/6.
 */
@Configuration
@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication
public class LocAdviceErrorAutoConfiguration {

  @ConditionalOnClass(MethodArgumentNotValidException.class)
  @Configuration
  @RestControllerAdvice
  static class MethodArgumentNotValidExceptionConfiguration {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    //验证requestbody失败异常的处理
    public BasicResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
      logError("methodArgumentNotValidException", e.getMessage(), e);
      return BasicResult.fail(BasicResultCode.METHOD_ARGUMENT_MISS_ERROR.getCode(),
          BasicResultCode.METHOD_ARGUMENT_MISS_ERROR.getMsg(),
          MethodArgumentNotValidExceptionHelper.firstErrorMessage(e.getBindingResult()));
    }


    private static class MethodArgumentNotValidExceptionHelper {

      static String firstErrorMessage(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream().findFirst()
            .map(ObjectError::getDefaultMessage).orElse("");
      }
    }
  }

  @ConditionalOnClass(ConstraintViolationException.class)
  @Configuration
  @RestControllerAdvice
  static class ConstraintViolationExceptionConfiguration {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    //对于接口参数requestParam的validate验证
    public BasicResult handleConstraintViolationException(ConstraintViolationException e) {
      logError("constraintViolationException", e.getMessage(), e);
      return BasicResult.fail(BasicResultCode.METHOD_ARGUMENT_VALIDATE_ERROR.getCode(),
          BasicResultCode.METHOD_ARGUMENT_VALIDATE_ERROR.getMsg(),
          ConstraintViolationExceptionHelper.firstErrorMessage(e.getConstraintViolations()));
    }

    static class ConstraintViolationExceptionHelper {

      static String firstErrorMessage(Set<ConstraintViolation<?>> constraintViolations) {
        return constraintViolations.stream().findFirst()
            .map(constraintViolation -> constraintViolation.getPropertyPath().toString() + ":"
                + constraintViolation.getMessage()).orElse("");
      }
    }
  }

  @ConditionalOnClass(MissingServletRequestParameterException.class)
  @Configuration
  @RestControllerAdvice
  //对于接口需要参数，但是没有传参数的异常处理逻辑
  static class MissingServletRequestParameterExceptionConfiguration {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public BasicResult handleMissingServletRequestParameterException(
        MissingServletRequestParameterException e) {
      logError("missingServletRequestParameterException", e.getMessage(), e);
      return BasicResult
          .fail(BasicResultCode.METHOD_ARGUMENT_MISS_ERROR.getCode(),
              BasicResultCode.METHOD_ARGUMENT_MISS_ERROR.getMsg(),
              String.format("参数%s未传", e.getParameterName()));
    }
  }

  @ConditionalOnClass(HttpMediaTypeNotSupportedException.class)
  @Configuration
  @RestControllerAdvice
  //对于传入的媒体类型不正确的异常处理
  static class HttpMediaTypeNotSupportedExceptionConfiguration {

    @ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public BasicResult handleHttpMediaTypeNotSupportedException(
        HttpMediaTypeNotSupportedException e) {
      logError("httpMediaTypeNotSupportedException", e.getMessage(), e);
      return BasicResult
          .fail(BasicResultCode.UNSUPPORTED_MEDIA_TYPE_ERROR.getCode(),
              BasicResultCode.UNSUPPORTED_MEDIA_TYPE_ERROR.getMsg(),
              String.format("媒体类型%s错误", e.getContentType()));
    }
  }


  @ConditionalOnClass(HttpRequestMethodNotSupportedException.class)
  @Configuration
  @RestControllerAdvice
  static class HttpRequestMethodNotSupportedExceptionConfiguration {

    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    //对于接口方法不匹配的异常处理
    public BasicResult handleHttpRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException e) {
      logError("httpRequestMethodNotSupportedException", e.getMessage(), e);
      return BasicResult
          .fail(BasicResultCode.METHOD_NOT_ALLOW_ERROR.getCode(),
              BasicResultCode.METHOD_NOT_ALLOW_ERROR.getMsg(),
              e.getMessage());
    }
  }


  @ConditionalOnClass(LocCommonException.class)
  @Configuration
  @RestControllerAdvice
  static class LocCommonExceptionConfiguration {

    @ResponseStatus(value = HttpStatus.OK)
    @ExceptionHandler(LocCommonException.class)
    //对于接口自定义异常的处理
    public BasicResult handleLocException(LocCommonException e) {
      logError("locException", e.getMessage(), e);
      return BasicResult.fail(e.getCode(), e.getMsg(), e.getDetailMsg());
    }
  }

  @ConditionalOnClass(SQLException.class)
  @Configuration
  @RestControllerAdvice
  static class SqlExceptionConfiguration {

    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(SQLException.class)
    public BasicResult handleSqlException(SQLException e) {
      logError("SQLException", e.getMessage(), e);
      return BasicResult
          .fail(BasicResultCode.SQL_ERROR.getCode(), BasicResultCode.SQL_ERROR.getMsg(),
              e.getMessage());
    }
  }

  @ConditionalOnClass(RedisException.class)
  @Configuration
  @RestControllerAdvice
  static class redisExceptionConfiguration {

    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(RedisException.class)
    public BasicResult handleRedisException(RedisException e) {
      logError("RedisException", e.getMessage(), e);
      return BasicResult
          .fail(BasicResultCode.REDIS_ERROR.getCode(), BasicResultCode.REDIS_ERROR.getMsg(),
              e.getMessage());
    }
  }


  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(RuntimeException.class)
  //对于接口运行时错误异常的处理
  public BasicResult handleRuntimeException(RuntimeException e) {
    logError("runtimeException", e.getMessage(), e);
    return BasicResult
        .fail(BasicResultCode.RUNTIME_ERROR.getCode(), BasicResultCode.RUNTIME_ERROR.getMsg(),
            e.getMessage());
  }

  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Throwable.class)
  //对于所有异常的处理
  public BasicResult handleThrowable(Throwable t) {
    logError("throwableError", t.getMessage(), t);
    return BasicResult
        .fail(BasicResultCode.THROWABLE_ERROR.getCode(), BasicResultCode.THROWABLE_ERROR.getMsg(),
            t.getMessage());
  }

  static void logError(String name, String msg, Throwable t) {
    log.error("error name is {}, msg is {}", name, msg, t);
  }
}
