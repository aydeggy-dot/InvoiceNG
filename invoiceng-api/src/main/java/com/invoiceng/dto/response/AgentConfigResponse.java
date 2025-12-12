package com.invoiceng.dto.response;

import com.invoiceng.entity.AgentConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentConfigResponse {

    private UUID id;
    private UUID businessId;
    private String agentName;
    private String greetingMessage;
    private Map<String, Object> personality;
    private Map<String, Object> salesSettings;
    private Map<String, Object> businessHours;
    private String afterHoursBehavior;
    private List<String> handoffTriggers;
    private String handoffNotificationMethod;
    private Map<String, String> templates;
    private List<Map<String, Object>> deliveryAreas;
    private BigDecimal defaultDeliveryFee;
    private String dispatchTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AgentConfigResponse fromEntity(AgentConfig config) {
        return AgentConfigResponse.builder()
                .id(config.getId())
                .businessId(config.getBusiness().getId())
                .agentName(config.getAgentName())
                .greetingMessage(config.getGreetingMessage())
                .personality(config.getPersonality())
                .salesSettings(config.getSalesSettings())
                .businessHours(config.getBusinessHours())
                .afterHoursBehavior(config.getAfterHoursBehavior())
                .handoffTriggers(config.getHandoffTriggers())
                .handoffNotificationMethod(config.getHandoffNotificationMethod())
                .templates(config.getTemplates())
                .deliveryAreas(config.getDeliveryAreas())
                .defaultDeliveryFee(config.getDefaultDeliveryFee())
                .dispatchTime(config.getDispatchTime())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}
