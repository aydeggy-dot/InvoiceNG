package com.invoiceng.entity;

public enum ReminderType {
    BEFORE_DUE("before_due"),
    ON_DUE("on_due"),
    AFTER_DUE("after_due");

    private final String value;

    ReminderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
