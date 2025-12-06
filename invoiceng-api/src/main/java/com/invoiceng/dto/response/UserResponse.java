package com.invoiceng.dto.response;

import com.invoiceng.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private String phone;
    private String email;
    private String businessName;
    private String businessAddress;
    private String bankName;
    private String bankCode;
    private String accountNumber;
    private String accountName;
    private String logoUrl;
    private String subscriptionTier;
    private Integer invoiceCountThisMonth;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .businessName(user.getBusinessName())
                .businessAddress(user.getBusinessAddress())
                .bankName(user.getBankName())
                .bankCode(user.getBankCode())
                .accountNumber(user.getAccountNumber())
                .accountName(user.getAccountName())
                .logoUrl(user.getLogoUrl())
                .subscriptionTier(user.getSubscriptionTier())
                .invoiceCountThisMonth(user.getInvoiceCountThisMonth())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
