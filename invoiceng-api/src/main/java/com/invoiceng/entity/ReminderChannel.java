package com.invoiceng.entity;

public enum ReminderChannel {
    WHATSAPP("whatsapp"),
    SMS("sms"),
    EMAIL("email");

    private final String value;

    ReminderChannel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
