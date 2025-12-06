package com.invoiceng.entity;

public enum InvoiceStatus {
    DRAFT("draft"),
    SENT("sent"),
    VIEWED("viewed"),
    PAID("paid"),
    OVERDUE("overdue"),
    CANCELLED("cancelled");

    private final String value;

    InvoiceStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static InvoiceStatus fromValue(String value) {
        for (InvoiceStatus status : InvoiceStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown invoice status: " + value);
    }
}
