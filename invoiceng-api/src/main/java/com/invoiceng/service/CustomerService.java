package com.invoiceng.service;

import com.invoiceng.dto.request.CreateCustomerRequest;
import com.invoiceng.dto.request.UpdateCustomerRequest;
import com.invoiceng.dto.response.CustomerResponse;
import com.invoiceng.dto.response.PaginatedResponse;
import com.invoiceng.entity.Customer;
import com.invoiceng.entity.User;
import com.invoiceng.exception.ResourceNotFoundException;
import com.invoiceng.exception.ValidationException;
import com.invoiceng.repository.CustomerRepository;
import com.invoiceng.repository.UserRepository;
import com.invoiceng.util.PhoneNumberFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PhoneNumberFormatter phoneFormatter;

    public PaginatedResponse<CustomerResponse> listCustomers(
            UUID userId,
            String search,
            int page,
            int limit,
            String sortBy,
            String sortOrder
    ) {
        Pageable pageable = createPageable(page, limit, sortBy, sortOrder);

        Page<Customer> customerPage;
        if (search != null && !search.isBlank()) {
            customerPage = customerRepository.searchByUserIdAndNameOrPhone(userId, search.trim(), pageable);
        } else {
            customerPage = customerRepository.findByUserId(userId, pageable);
        }

        return PaginatedResponse.fromPage(customerPage, CustomerResponse::fromEntity);
    }

    public CustomerResponse getCustomer(UUID customerId, UUID userId) {
        Customer customer = customerRepository.findByIdAndUserId(customerId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        return CustomerResponse.fromEntity(customer);
    }

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request, UUID userId) {
        String formattedPhone = phoneFormatter.formatToInternational(request.getPhone());

        // Check for duplicate phone
        if (customerRepository.existsByUserIdAndPhone(userId, formattedPhone)) {
            throw new ValidationException("A customer with this phone number already exists");
        }

        User user = userRepository.getReferenceById(userId);

        Customer customer = Customer.builder()
                .user(user)
                .name(request.getName().trim())
                .phone(formattedPhone)
                .email(request.getEmail())
                .address(request.getAddress())
                .notes(request.getNotes())
                .build();

        customer = customerRepository.save(customer);
        log.info("Created customer {} for user {}", customer.getId(), userId);

        return CustomerResponse.fromEntity(customer);
    }

    @Transactional
    public CustomerResponse updateCustomer(UUID customerId, UpdateCustomerRequest request, UUID userId) {
        Customer customer = customerRepository.findByIdAndUserId(customerId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        if (request.getName() != null) {
            customer.setName(request.getName().trim());
        }

        if (request.getPhone() != null) {
            String formattedPhone = phoneFormatter.formatToInternational(request.getPhone());
            // Check for duplicate phone (excluding current customer)
            customerRepository.findByUserIdAndPhone(userId, formattedPhone)
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(customerId)) {
                            throw new ValidationException("A customer with this phone number already exists");
                        }
                    });
            customer.setPhone(formattedPhone);
        }

        if (request.getEmail() != null) {
            customer.setEmail(request.getEmail());
        }

        if (request.getAddress() != null) {
            customer.setAddress(request.getAddress());
        }

        if (request.getNotes() != null) {
            customer.setNotes(request.getNotes());
        }

        customer = customerRepository.save(customer);
        log.info("Updated customer {}", customerId);

        return CustomerResponse.fromEntity(customer);
    }

    @Transactional
    public void deleteCustomer(UUID customerId, UUID userId) {
        Customer customer = customerRepository.findByIdAndUserId(customerId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        customerRepository.delete(customer);
        log.info("Deleted customer {}", customerId);
    }

    public Customer getOrCreateCustomer(UUID userId, UUID customerId, CreateCustomerRequest customerData) {
        if (customerId != null) {
            return customerRepository.findByIdAndUserId(customerId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        }

        if (customerData == null) {
            throw new ValidationException("Either customerId or customerData must be provided");
        }

        String formattedPhone = phoneFormatter.formatToInternational(customerData.getPhone());

        // Try to find existing customer by phone
        return customerRepository.findByUserIdAndPhone(userId, formattedPhone)
                .orElseGet(() -> {
                    // Create new customer
                    User user = userRepository.getReferenceById(userId);
                    Customer customer = Customer.builder()
                            .user(user)
                            .name(customerData.getName().trim())
                            .phone(formattedPhone)
                            .email(customerData.getEmail())
                            .address(customerData.getAddress())
                            .notes(customerData.getNotes())
                            .build();
                    return customerRepository.save(customer);
                });
    }

    public List<CustomerResponse> getTopCustomers(UUID userId, int limit) {
        return customerRepository.findTopCustomersByTotalPaid(userId, limit)
                .stream()
                .map(CustomerResponse::fromEntity)
                .toList();
    }

    private Pageable createPageable(int page, int limit, String sortBy, String sortOrder) {
        // Validate and default parameters
        page = Math.max(1, page) - 1; // Convert to 0-based
        limit = Math.min(Math.max(1, limit), 100);

        // Map sort field
        String sortField = switch (sortBy != null ? sortBy.toLowerCase() : "createdat") {
            case "name" -> "name";
            case "totalpaid" -> "totalPaid";
            case "totalinvoices" -> "totalInvoices";
            default -> "createdAt";
        };

        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;

        return PageRequest.of(page, limit, Sort.by(direction, sortField));
    }
}
