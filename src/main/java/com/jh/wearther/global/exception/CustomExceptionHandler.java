package com.jh.wearther.global.exception;

import static com.jh.wearther.global.exception.ErrorCode.INTERNAL_SERVER_ERROR;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler({CustomException.class, MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class})
  protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
    return new ResponseEntity<>(
        new ErrorResponse(e.getErrorCode().getStatus(), e.getErrorCode().getDescription()),
            HttpStatus.valueOf(e.getErrorCode().getStatus())
    );
  }

  @ExceptionHandler({Exception.class})
  protected ResponseEntity<ErrorResponse> handleException(Exception e) {
    return new ResponseEntity<>(
        new ErrorResponse(INTERNAL_SERVER_ERROR.getStatus(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR
    );
  }
}
