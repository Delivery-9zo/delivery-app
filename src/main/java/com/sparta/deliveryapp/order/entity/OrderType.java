package com.sparta.deliveryapp.order.entity;

public enum OrderType {

    FACE_TO_FACE("FACE_TO_FACE"),
    NON_FACE_TO_FACE("NON_FACE_TO_FACE");

    private final String type;

    OrderType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public static class Type {
        private static final String FACE_TO_FACE = "FACE_TO_FACE";
        private static final String NON_FACE_TO_FACE = "NON_FACE_TO_FACE";
    }
}
