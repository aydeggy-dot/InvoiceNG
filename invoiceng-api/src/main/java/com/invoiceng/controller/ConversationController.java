package com.invoiceng.controller;

import com.invoiceng.dto.response.ApiResponse;
import com.invoiceng.dto.response.ConversationMessageResponse;
import com.invoiceng.dto.response.ConversationResponse;
import com.invoiceng.dto.response.PaginatedResponse;
import com.invoiceng.dto.whatsapp.WhatsAppSendResponse;
import com.invoiceng.entity.Conversation;
import com.invoiceng.entity.ConversationMessage;
import com.invoiceng.entity.User;
import com.invoiceng.exception.ResourceNotFoundException;
import com.invoiceng.repository.ConversationRepository;
import com.invoiceng.repository.UserRepository;
import com.invoiceng.security.CurrentUser;
import com.invoiceng.security.UserPrincipal;
import com.invoiceng.service.ConversationService;
import com.invoiceng.service.WhatsAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Conversations", description = "WhatsApp conversation management endpoints")
public class ConversationController {

    private final ConversationService conversationService;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final WhatsAppService whatsAppService;

    @GetMapping
    @Operation(summary = "List conversations", description = "Get paginated list of conversations")
    public ResponseEntity<ApiResponse<PaginatedResponse<ConversationResponse>>> listConversations(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean handedOff,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "lastMessageAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        Page<Conversation> conversationPage;

        if (Boolean.TRUE.equals(handedOff)) {
            conversationPage = conversationRepository.findByBusinessIdAndIsHandedOffTrue(currentUser.getId(), pageable);
        } else if ("active".equals(status)) {
            conversationPage = conversationRepository.findByBusinessIdAndIsActiveTrue(currentUser.getId(), pageable);
        } else {
            conversationPage = conversationRepository.findByBusinessId(currentUser.getId(), pageable);
        }

        PaginatedResponse<ConversationResponse> response = PaginatedResponse.fromPage(
                conversationPage,
                ConversationResponse::fromEntityBasic
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get conversation", description = "Get conversation details by ID")
    public ResponseEntity<ApiResponse<ConversationResponse>> getConversation(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id
    ) {
        Conversation conversation = conversationRepository.findById(id)
                .filter(c -> c.getBusiness().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", id));

        return ResponseEntity.ok(ApiResponse.success(ConversationResponse.fromEntity(conversation)));
    }

    @GetMapping("/{id}/messages")
    @Operation(summary = "Get conversation messages", description = "Get all messages for a conversation")
    public ResponseEntity<ApiResponse<List<ConversationMessageResponse>>> getMessages(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id,
            @RequestParam(defaultValue = "100") int limit
    ) {
        Conversation conversation = conversationRepository.findById(id)
                .filter(c -> c.getBusiness().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", id));

        List<ConversationMessage> messages = conversationService.getRecentMessages(id, limit);

        List<ConversationMessageResponse> response = messages.stream()
                .map(ConversationMessageResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/messages")
    @Operation(summary = "Send message", description = "Send a message to the customer")
    public ResponseEntity<ApiResponse<ConversationMessageResponse>> sendMessage(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id,
            @RequestBody SendMessageRequest request
    ) {
        Conversation conversation = conversationRepository.findById(id)
                .filter(c -> c.getBusiness().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", id));

        User business = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        // Send via WhatsApp
        String phoneNumberId = whatsAppService.getPhoneNumberId(business);
        String accessToken = whatsAppService.getAccessToken(business);
        WhatsAppSendResponse sendResponse = whatsAppService.sendTextMessage(
                phoneNumberId,
                accessToken,
                conversation.getCustomerPhone(),
                request.content()
        );

        // Extract message ID from response
        String whatsappMessageId = null;
        if (sendResponse != null && sendResponse.getMessages() != null && !sendResponse.getMessages().isEmpty()) {
            whatsappMessageId = sendResponse.getMessages().get(0).getId();
        }

        // Save the message
        ConversationMessage message = conversationService.saveOutboundMessage(
                conversation,
                request.content(),
                "text",
                whatsappMessageId
        );

        return ResponseEntity.ok(ApiResponse.success(
                ConversationMessageResponse.fromEntity(message),
                "Message sent successfully"
        ));
    }

    @PostMapping("/{id}/handoff")
    @Operation(summary = "Request handoff", description = "Mark conversation for human handoff")
    public ResponseEntity<ApiResponse<ConversationResponse>> requestHandoff(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id,
            @RequestBody(required = false) HandoffRequest request
    ) {
        Conversation conversation = conversationRepository.findById(id)
                .filter(c -> c.getBusiness().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", id));

        String reason = request != null ? request.reason() : "Manual handoff requested";
        conversation = conversationService.requestHandoff(id, reason);

        return ResponseEntity.ok(ApiResponse.success(
                ConversationResponse.fromEntity(conversation),
                "Conversation marked for handoff"
        ));
    }

    @PostMapping("/{id}/resolve")
    @Operation(summary = "Resolve handoff", description = "Mark handoff as resolved and return to AI")
    public ResponseEntity<ApiResponse<ConversationResponse>> resolveHandoff(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id
    ) {
        Conversation conversation = conversationRepository.findById(id)
                .filter(c -> c.getBusiness().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", id));

        conversation.setIsHandedOff(false);
        conversation.setHandedOffAt(null);
        conversation.setHandedOffReason(null);
        conversation = conversationRepository.save(conversation);

        return ResponseEntity.ok(ApiResponse.success(
                ConversationResponse.fromEntity(conversation),
                "Handoff resolved"
        ));
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "Close conversation", description = "Mark conversation as closed/abandoned")
    public ResponseEntity<ApiResponse<ConversationResponse>> closeConversation(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable UUID id
    ) {
        Conversation conversation = conversationRepository.findById(id)
                .filter(c -> c.getBusiness().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", id));

        conversation = conversationService.markAsAbandoned(id);

        return ResponseEntity.ok(ApiResponse.success(
                ConversationResponse.fromEntity(conversation),
                "Conversation closed"
        ));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active conversations", description = "Get all active conversations")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getActiveConversations(
            @CurrentUser UserPrincipal currentUser
    ) {
        List<Conversation> conversations = conversationService.getActiveConversations(currentUser.getId());

        List<ConversationResponse> response = conversations.stream()
                .map(ConversationResponse::fromEntityBasic)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/handoff")
    @Operation(summary = "Get handoff conversations", description = "Get conversations requiring human attention")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getHandoffConversations(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("handedOffAt").descending());
        Page<Conversation> conversations = conversationService.getHandoffConversations(currentUser.getId(), pageable);

        List<ConversationResponse> response = conversations.getContent().stream()
                .map(ConversationResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Request records
    public record SendMessageRequest(String content) {}
    public record HandoffRequest(String reason) {}
}
