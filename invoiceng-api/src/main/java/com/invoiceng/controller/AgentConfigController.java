package com.invoiceng.controller;

import com.invoiceng.dto.request.UpdateAgentConfigRequest;
import com.invoiceng.dto.response.AgentConfigResponse;
import com.invoiceng.dto.response.ApiResponse;
import com.invoiceng.security.CurrentUser;
import com.invoiceng.security.UserPrincipal;
import com.invoiceng.service.AgentConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agent-config")
@RequiredArgsConstructor
@Tag(name = "Agent Configuration", description = "AI Agent configuration endpoints")
public class AgentConfigController {

    private final AgentConfigService agentConfigService;

    @GetMapping
    @Operation(summary = "Get agent configuration", description = "Get AI agent configuration for the business")
    public ResponseEntity<ApiResponse<AgentConfigResponse>> getAgentConfig(
            @CurrentUser UserPrincipal currentUser
    ) {
        AgentConfigResponse response = agentConfigService.getOrCreateAgentConfig(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping
    @Operation(summary = "Update agent configuration", description = "Update AI agent configuration")
    public ResponseEntity<ApiResponse<AgentConfigResponse>> updateAgentConfig(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody UpdateAgentConfigRequest request
    ) {
        AgentConfigResponse response = agentConfigService.updateAgentConfig(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Agent configuration updated successfully"));
    }

    @DeleteMapping
    @Operation(summary = "Reset agent configuration", description = "Delete agent configuration and reset to defaults")
    public ResponseEntity<ApiResponse<Void>> resetAgentConfig(
            @CurrentUser UserPrincipal currentUser
    ) {
        agentConfigService.deleteAgentConfig(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Agent configuration reset successfully"));
    }
}
