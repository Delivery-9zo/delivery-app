package com.sparta.deliveryapp.commons.exception.error;

import com.sparta.deliveryapp.commons.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException{
    private final ErrorCode errorCode;
}
