package com.invoiceng.entity;

public enum ReminderStatus {
    PENDING("pending"),
    SENT("sent"),
    FAILED("failed"),
    CANCELLED("cancelled");

    private final String value;

    ReminderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
