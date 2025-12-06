package com.invoiceng.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestOtpRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+234|234|0)[789]\\d{9}$", message = "Invalid Nigerian phone number")
    private String phone;
}
