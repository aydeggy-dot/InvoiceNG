package com.invoiceng.util;

import org.springframework.stereotype.Component;

@Component
public class PhoneNumberFormatter {

    /**
     * Formats a Nigerian phone number to international format (234xxxxxxxxxx)
     * Accepts formats: 08012345678, +2348012345678, 2348012345678
     */
    public String formatToInternational(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }

        // Remove all spaces, dashes, and parentheses
        String cleaned = phone.replaceAll("[\\s\\-()]", "");

        // Remove leading + if present
        if (cleaned.startsWith("+")) {
            cleaned = cleaned.substring(1);
        }

        // Convert 0xxx to 234xxx
        if (cleaned.startsWith("0") && cleaned.length() == 11) {
            cleaned = "234" + cleaned.substring(1);
        }

        // Validate the result
        if (!cleaned.matches("^234[789]\\d{9}$")) {
            throw new IllegalArgumentException("Invalid Nigerian phone number format");
        }

        return cleaned;
    }

    /**
     * Formats a phone number for display (e.g., 0801 234 5678)
     */
    public String formatForDisplay(String phone) {
        String international = formatToInternational(phone);
        // Convert 234xxxxxxxxxx to 0xxx xxx xxxx
        String local = "0" + international.substring(3);
        return local.substring(0, 4) + " " + local.substring(4, 7) + " " + local.substring(7);
    }

    /**
     * Validates if the phone number is a valid Nigerian number
     */
    public boolean isValid(String phone) {
        try {
            formatToInternational(phone);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
