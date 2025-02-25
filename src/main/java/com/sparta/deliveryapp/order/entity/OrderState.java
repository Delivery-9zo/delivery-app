package com.sparta.deliveryapp.order.entity;

public enum OrderState {

    WAIT("WAIT"),
    SUCCESS("SUCCESS"),
    CANCEL("CANCEL");

    private final String state;

    OrderState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public static class State {
        public static final String WAIT = "WAIT";
        public static final String SUCCESS = "SUCCESS";
        public static final String CANCEL = "CANCEL";
    }
}
