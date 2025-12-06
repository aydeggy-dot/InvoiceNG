package com.invoiceng.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequest {

    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+234|234|0)[789]\\d{9}$", message = "Invalid Nigerian phone number")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 500, message = "Address must be less than 500 characters")
    private String address;

    @Size(max = 1000, message = "Notes must be less than 1000 characters")
    private String notes;
}
