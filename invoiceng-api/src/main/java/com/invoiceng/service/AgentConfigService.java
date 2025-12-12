package com.invoiceng.service;

import com.invoiceng.dto.request.UpdateAgentConfigRequest;
import com.invoiceng.dto.response.AgentConfigResponse;
import com.invoiceng.entity.AgentConfig;
import com.invoiceng.entity.User;
import com.invoiceng.repository.AgentConfigRepository;
import com.invoiceng.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AgentConfigService {

    private final AgentConfigRepository agentConfigRepository;
    private final UserRepository userRepository;

    public AgentConfigResponse getAgentConfig(UUID businessId) {
        AgentConfig config = agentConfigRepository.findByBusinessId(businessId)
                .orElse(null);

        if (config == null) {
            return null;
        }

        return AgentConfigResponse.fromEntity(config);
    }

    @Transactional
    public AgentConfigResponse getOrCreateAgentConfig(UUID businessId) {
        AgentConfig config = agentConfigRepository.findByBusinessId(businessId)
                .orElseGet(() -> createDefaultConfig(businessId));

        return AgentConfigResponse.fromEntity(config);
    }

    @Transactional
    public AgentConfigResponse updateAgentConfig(UUID businessId, UpdateAgentConfigRequest request) {
        AgentConfig config = agentConfigRepository.findByBusinessId(businessId)
                .orElseGet(() -> createDefaultConfig(businessId));

        if (request.getAgentName() != null) {
            config.setAgentName(request.getAgentName());
        }
        if (request.getGreetingMessage() != null) {
            config.setGreetingMessage(request.getGreetingMessage());
        }
        if (request.getPersonality() != null) {
            config.setPersonality(request.getPersonality());
        }
        if (request.getSalesSettings() != null) {
            config.setSalesSettings(request.getSalesSettings());
        }
        if (request.getBusinessHours() != null) {
            config.setBusinessHours(request.getBusinessHours());
        }
        if (request.getAfterHoursBehavior() != null) {
            config.setAfterHoursBehavior(request.getAfterHoursBehavior());
        }
        if (request.getHandoffTriggers() != null) {
            config.setHandoffTriggers(request.getHandoffTriggers());
        }
        if (request.getHandoffNotificationMethod() != null) {
            config.setHandoffNotificationMethod(request.getHandoffNotificationMethod());
        }
        if (request.getTemplates() != null) {
            config.setTemplates(request.getTemplates());
        }
        if (request.getDeliveryAreas() != null) {
            config.setDeliveryAreas(request.getDeliveryAreas());
        }
        if (request.getDefaultDeliveryFee() != null) {
            config.setDefaultDeliveryFee(request.getDefaultDeliveryFee());
        }
        if (request.getDispatchTime() != null) {
            config.setDispatchTime(request.getDispatchTime());
        }

        config = agentConfigRepository.save(config);
        log.info("Updated agent config for business {}", businessId);

        return AgentConfigResponse.fromEntity(config);
    }

    @Transactional
    public void deleteAgentConfig(UUID businessId) {
        agentConfigRepository.findByBusinessId(businessId)
                .ifPresent(config -> {
                    agentConfigRepository.delete(config);
                    log.info("Deleted agent config for business {}", businessId);
                });
    }

    public AgentConfig getAgentConfigEntity(UUID businessId) {
        return agentConfigRepository.findByBusinessId(businessId).orElse(null);
    }

    private AgentConfig createDefaultConfig(UUID businessId) {
        User business = userRepository.getReferenceById(businessId);

        AgentConfig config = AgentConfig.builder()
                .business(business)
                .agentName("AI Sales Assistant")
                .greetingMessage("Hello! Welcome to our store. How can I help you today?")
                .build();

        config = agentConfigRepository.save(config);
        log.info("Created default agent config for business {}", businessId);

        return config;
    }
}
