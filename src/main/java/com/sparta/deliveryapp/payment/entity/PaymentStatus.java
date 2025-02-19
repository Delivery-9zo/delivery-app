package com.sparta.deliveryapp.payment.entity;

public enum PaymentStatus {

    SUCCESS("SUCCESS"),
    CANCEL("CANCEL");

    private String status;

    PaymentStatus(String status) {
        this.status = status;
    }

    public static class Status {
        public static final String SUCCESS = "SUCCESS";
        public static final String CANCEL = "CANCEL";
    }
}
