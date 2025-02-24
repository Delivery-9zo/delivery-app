package com.sparta.deliveryapp.commons.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 4**에러
    REQUEST_PARAMETER_BIND_FAILED(HttpStatus.BAD_REQUEST, "REQ_001", "PARAMETER_BIND_FAILED"),
    NON_ZERO_PARAMETER(HttpStatus.BAD_REQUEST, "REQ_002", "파라미터로 0이 넘어오면 안됩니다."),

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORD_001", "존재하지 않는 주문건입니다."),

    ITEM_SOLD_OUT(HttpStatus.NOT_FOUND, "ITEM_001", "아이템 수량이 0입니다."),

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "회원이 존재하지 않습니다."),
    USER_DELETED(HttpStatus.FORBIDDEN, "USER_002", "이 사용자는 삭제된 사용자입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER_003", "비밀번호가 다릅니다."),
    EMAIL_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "USER_004", "이미 등록된 이메일입니다."),

    // 권한 관련 에러
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_001", "사용자 정보를 수정할 권한이 없습니다.");


    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(final HttpStatus status, final String code, final String message){
        this.status = status;
        this.message = message;
        this.code = code;
    }


}
