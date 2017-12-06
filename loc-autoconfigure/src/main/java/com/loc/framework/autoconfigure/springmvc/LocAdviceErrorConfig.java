package com.loc.framework.autoconfigure.springmvc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
  static class MethodArgumentNotValidExceptionConfiguration {

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
}
