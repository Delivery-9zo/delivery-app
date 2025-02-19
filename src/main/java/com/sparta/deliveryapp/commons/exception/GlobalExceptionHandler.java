package com.sparta.deliveryapp.commons.exception;

import com.sparta.deliveryapp.commons.dto.ErrorDto;
import com.sparta.deliveryapp.commons.exception.error.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.BindException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity<?> handleCustomException(CustomException e) {
        log.error("handleCustomException throw CustomException : {}", e.getErrorCode());
        ErrorDto error = ErrorDto.builder()
                .status(e.getErrorCode().getStatus().value())
                .message(e.getErrorCode().getMessage())
                .code(e.getErrorCode().getCode())
                .build();

        log.info("error :: {}", error);

        return new ResponseEntity<>(error, e.getErrorCode().getStatus());
    }

}
