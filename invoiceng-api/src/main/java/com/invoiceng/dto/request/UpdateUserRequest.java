package com.invoiceng.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 2, max = 255, message = "Business name must be between 2 and 255 characters")
    private String businessName;

    @Size(max = 500, message = "Business address must be less than 500 characters")
    private String businessAddress;

    @Size(max = 100, message = "Bank name must be less than 100 characters")
    private String bankName;

    @Size(max = 10, message = "Bank code must be less than 10 characters")
    private String bankCode;

    @Size(max = 20, message = "Account number must be less than 20 characters")
    private String accountNumber;

    @Size(max = 255, message = "Account name must be less than 255 characters")
    private String accountName;
}
