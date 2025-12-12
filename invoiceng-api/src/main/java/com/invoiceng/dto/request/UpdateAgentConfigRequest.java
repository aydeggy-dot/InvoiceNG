package com.invoiceng.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAgentConfigRequest {

    @Size(max = 100, message = "Agent name must be less than 100 characters")
    private String agentName;

    private String greetingMessage;

    private Map<String, Object> personality;

    private Map<String, Object> salesSettings;

    private Map<String, Object> businessHours;

    @Size(max = 20, message = "After hours behavior must be less than 20 characters")
    private String afterHoursBehavior;

    private List<String> handoffTriggers;

    @Size(max = 20, message = "Handoff notification method must be less than 20 characters")
    private String handoffNotificationMethod;

    private Map<String, String> templates;

    private List<Map<String, Object>> deliveryAreas;

    private BigDecimal defaultDeliveryFee;

    @Size(max = 100, message = "Dispatch time must be less than 100 characters")
    private String dispatchTime;
}
