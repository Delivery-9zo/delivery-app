package com.sparta.deliveryapp.commons.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 4**에러
    REQUEST_PARAMETER_BIND_FAILED(HttpStatus.BAD_REQUEST, "REQ_001", "PARAMETER_BIND_FAILED"),
    NON_ZERO_PARAMETER(HttpStatus.BAD_REQUEST, "REQ_002", "파라미터로 0이 넘어오면 안됩니다."),

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORD_001", "존재하지 않는 주문건입니다."),

    ITEM_SOLD_OUT(HttpStatus.NOT_FOUND, "ITEM_001", "아이템 수량이 0입니다.");

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(final HttpStatus status, final String code, final String message){
        this.status = status;
        this.message = message;
        this.code = code;
    }


}
