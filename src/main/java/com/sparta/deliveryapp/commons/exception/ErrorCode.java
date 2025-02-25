package com.sparta.deliveryapp.commons.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

  // 4**에러
  REQUEST_PARAMETER_BIND_FAILED(HttpStatus.BAD_REQUEST, "REQ_001", "PARAMETER_BIND_FAILED"),
  NON_ZERO_PARAMETER(HttpStatus.BAD_REQUEST, "REQ_002", "파라미터로 0이 넘어오면 안됩니다."),

    ITEM_SOLD_OUT(HttpStatus.NOT_FOUND, "ITEM_001", "아이템 수량이 0입니다."),

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "회원이 존재하지 않습니다."),
    USER_DELETED(HttpStatus.FORBIDDEN, "USER_002", "이 사용자는 삭제된 사용자입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER_003", "비밀번호가 다릅니다."),
    EMAIL_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "USER_004", "이미 등록된 이메일입니다."),

    // 권한 관련 에러
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_001", "사용자 정보를 수정할 권한이 없습니다."),
    ACCESS_DENIED_ONLY_CUSTOMER(HttpStatus.FORBIDDEN, "AUTH_002", "CUSTOMER 권한을 가진 사용자만 조회 가능합니다."),

  //상점 도메인 에러
  ALREADY_REGISTERED_STORE_ID(HttpStatus.BAD_REQUEST, "S-001", "이미 존재하는 가게 ID입니다."),
  NOT_EXISTS_STORE_ID(HttpStatus.NOT_FOUND, "S-002", "존재하지 않는 가게 ID입니다."),
  NOT_EXISTS_STORE_NAME(HttpStatus.NOT_FOUND, "S-003", "존재하지 않는 가게 ID입니다."),
  ALREADY_DELETED_STORE_ID(HttpStatus.BAD_REQUEST, "S-004", "이미 삭제된 가게입니다."),
  STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "S-005", "가게를 찾을 수 없습니다."),
  INVALILD_LOCATION_DATA(HttpStatus.BAD_REQUEST, "S-006", "좌표의 자릿수를 확인해주세요."),

  // 메뉴 도메인 에러
  MENU_NOT_EXISTS_STORE_ID(HttpStatus.NOT_FOUND, "M-001", "존재하지 않는 가게 ID입니다."),
  MENU_NOT_EXISTS_MENU_ID(HttpStatus.NOT_FOUND, "M-002", "존재하지 않는 메뉴 ID입니다."),
  MENU_NOT_EMPLOYEE_THIS_SHOP(HttpStatus.FORBIDDEN, "M-003", "이 상점에 접근 권한이 없는 사용자입니다."),

  // 리뷰 도메인 에러
  REVIEW_NOT_ORDER_SUCCESS(HttpStatus.BAD_REQUEST, "REV-001", "주문 완료된 상태에서만 리뷰를 작성할 수 있습니다."),
  REVIEW_NOT_EXISTS_USER_ID(HttpStatus.NOT_FOUND, "REV-002", "존재하지 않는 유저 ID입니다."),

  //카테고리 도메인 에러
  ALREADY_REGISTERED_CATEGORY(HttpStatus.BAD_REQUEST, "C-001", "이미 존재하는 카테고리입니다."),
  REGISTERED_FAILED_CATEGORY(HttpStatus.BAD_REQUEST, "C-002", "카테고리 저장에 실패했습니다."),
  NOT_EXISTS_INPUT_CATEGORY_DATA(HttpStatus.NOT_FOUND, "C-003", "입력 정보 중 빈 값이 있습니다."),
  NOT_EXISTS_CATEGORY(HttpStatus.NOT_FOUND, "C-004", "존재하지 않는 카테고리입니다."),
  UPDATED_FAILED_CATEGORY(HttpStatus.BAD_REQUEST, "C-005", "카테고리 저장에 실패했습니다."),


  // 주문 관련 에러
  NOT_EXISTS_ORDER_ID(HttpStatus.NOT_FOUND, "ORD-001", "존재하지 않는 주문 ID 입니다."),
  ORDER_STATUS_FAILED_ORDER(HttpStatus.BAD_REQUEST, "ORD-002", "주문완료 상태가 아니므로 주문취소가 불가능합니다."),
  NOT_REGISTER_ORDER_STATUS(HttpStatus.BAD_REQUEST, "ORD-002", "주문대기 및 취소 상태로 주문완료가 불가능합니다."),
  AFTER_FIVE_ORDER_STATUS_FAILED_ORDER(HttpStatus.INTERNAL_SERVER_ERROR, "ORD-003", "주문취소는 주문완료 후 5분 이내에만 취소 가능합니다."),
  NOT_EXISTS_MENU_ID(HttpStatus.NOT_FOUND, "ORD-004", "해당 메뉴가 존재하지 않습니다."),
  ONLY_NON_FACE_ORDER_FAILED_ORDER(HttpStatus.BAD_REQUEST, "ORD-005", "배달 주문만 결제 가능합니다. 대면 주문과 결제는 가게에 문의해주세요!"),
  ONLY_FACE_ORDER_FAILED_ORDER(HttpStatus.BAD_REQUEST, "ORD-005", "비대면 주문과 결제는 CUSTOMER 만 가능합니다."),

  // 주문상세 관련 에러
  NOT_EXISTS_ITEM_ID(HttpStatus.NOT_FOUND, "ITM-001", "존재하지 않는 주문상세 ID 입니다."),

  // 결제 관련 에러
  NOT_EXISTS_PAYMENT_ID(HttpStatus.NOT_FOUND, "PAY-001", "존재하지 않는 결제 ID 입니다."),
  NOT_EXISTS_PAYMENT_USER_ID(HttpStatus.NOT_FOUND, "PAY-002", "결제한 회원이 존재하지 않습니다."),
  ACCESS_DENIED_ONLY_USER_ID(HttpStatus.NOT_FOUND, "PAY_003", "본인의 결제 정보만 조회 가능합니다."),
  NOT_SUFFICIENT_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "PAY_004", "결제 금액은 1원 이상이어야 합니다."),
  NOT_PAYMENT(HttpStatus.BAD_REQUEST, "PAY_005", "결제 중 오류가 발생했습니다."),
  NOT_PAYMENT_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAY_006", "결제 처리 중 오류가 발생했습니다.");



  private final String code;
  private final String message;
  private final HttpStatus status;

  ErrorCode(final HttpStatus status, final String code, final String message) {
    this.status = status;
    this.message = message;
    this.code = code;
  }


}
