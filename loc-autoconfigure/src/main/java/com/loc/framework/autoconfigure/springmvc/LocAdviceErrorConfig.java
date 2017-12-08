package com.loc.framework.autoconfigure.springmvc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Created on 2017/12/6.
 */
@Configuration
@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication
public class LocAdviceErrorConfig {

  @ConditionalOnClass(MethodArgumentNotValidException.class)
  @Configuration
  @RestControllerAdvice
  public static class MethodArgumentNotValidExceptionConfiguration {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BasicResult handMethodArgumentNotValidException(MethodArgumentNotValidException e) {
      return BasicResult.fail(BasicResultCode.METHOD_ARGUMENT_ERROR.getCode(),
          BasicResultCode.METHOD_ARGUMENT_ERROR.getMsg(),
          MethodArgumentNotValidExceptionHelper.firstErrorMessage(e.getBindingResult()));
    }

    private static class MethodArgumentNotValidExceptionHelper {

      static String firstErrorMessage(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream().findFirst()
            .map(ObjectError::getDefaultMessage).orElse("");
      }
    }
  }

  @ConditionalOnClass(MissingServletRequestParameterException.class)
  @Configuration
  @RestControllerAdvice
  public static class MissingServletRequestParameterExceptionConfiguration {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public BasicResult handleMissingServletRequestParameterException(
        MissingServletRequestParameterException e) {
      return BasicResult
          .fail(BasicResultCode.METHOD_ARGUMENT_ERROR.getCode(),
              BasicResultCode.METHOD_ARGUMENT_ERROR.getMsg(),
              String.format("参数%s未传", e.getParameterName()));
    }
  }

  @ConditionalOnClass(RuntimeException.class)
  @Configuration
  @RestControllerAdvice
  public static class RuntimeExceptionConfiguration {

    @ExceptionHandler(RuntimeException.class)
    public BasicResult handleRuntimeException(RuntimeException e) {
      return BasicResult.fail(BasicResultCode.RUNTIME_ERROR.getCode(), e.getMessage());
    }
  }

  @ConditionalOnClass(LocCommonException.class)
  @Configuration
  @RestControllerAdvice
  public static class LocCommonExceptionConfiguration {

    @ExceptionHandler(LocCommonException.class)
    public BasicResult handleLocException(LocCommonException e) {
      return BasicResult.fail(e.getCode(), e.getMsg());
    }
  }
}
