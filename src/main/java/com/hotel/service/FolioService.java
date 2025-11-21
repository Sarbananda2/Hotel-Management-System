package com.hotel.service;

import com.hotel.dto.AddLineItemRequest;
import com.hotel.dto.PaymentRequest;
import com.hotel.model.*;
import com.hotel.repository.*;
import com.hotel.security.RoleChecker;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class FolioService {
    private final FolioRepository folioRepository;
    private final FolioLineItemRepository lineItemRepository;
    private final PaymentRepository paymentRepository;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final RoleChecker roleChecker;

    public FolioService(FolioRepository folioRepository, FolioLineItemRepository lineItemRepository,
                       PaymentRepository paymentRepository, AuditLogRepository auditLogRepository,
                       UserRepository userRepository, RoleChecker roleChecker) {
        this.folioRepository = folioRepository;
        this.lineItemRepository = lineItemRepository;
        this.paymentRepository = paymentRepository;
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
        this.roleChecker = roleChecker;
    }

    public Folio getFolio(Integer id) {
        Optional<Folio> folioOpt = folioRepository.findById(id);
        if (folioOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Folio not found");
        }

        Folio folio = folioOpt.get();
        
        // Load line items
        folio.setLineItems(lineItemRepository.findByFolioId(id));
        
        // Load payments
        folio.setPayments(paymentRepository.findByFolioId(id));

        return folio;
    }

    @Transactional
    public FolioLineItem addLineItem(Integer folioId, AddLineItemRequest request) {
        Optional<Folio> folioOpt = folioRepository.findById(folioId);
        if (folioOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Folio not found");
        }

        FolioLineItem item = new FolioLineItem();
        item.setFolioId(folioId);
        item.setType(request.getType());
        item.setDescription(request.getDescription());
        item.setAmount(request.getAmount());

        FolioLineItem created = lineItemRepository.create(item);

        // Audit log
        Integer userId = getCurrentUserId();
        auditLogRepository.create(createAuditLog(userId, "ADD_LINE_ITEM", "FOLIO_LINE_ITEM", created.getId(), 
            "Added line item to folio " + folioId));

        return created;
    }

    @Transactional
    public Payment recordPayment(Integer folioId, PaymentRequest request) {
        Optional<Folio> folioOpt = folioRepository.findById(folioId);
        if (folioOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Folio not found");
        }

        // Only CASH payments are allowed
        if (!"CASH".equals(request.getMethod())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only CASH payments are allowed");
        }

        Payment payment = new Payment();
        payment.setFolioId(folioId);
        payment.setAmount(request.getAmount());
        payment.setMethod("CASH");
        payment.setReference(request.getReference());

        Payment created = paymentRepository.create(payment);

        // Audit log
        Integer userId = getCurrentUserId();
        auditLogRepository.create(createAuditLog(userId, "RECORD_PAYMENT", "PAYMENT", created.getId(), 
            "Recorded CASH payment of " + request.getAmount() + " for folio " + folioId));

        return created;
    }

    private Integer getCurrentUserId() {
        String email = roleChecker.getCurrentUserEmail();
        if (email != null) {
            return userRepository.findByEmail(email)
                    .map(User::getId)
                    .orElse(null);
        }
        return null;
    }

    private AuditLog createAuditLog(Integer userId, String action, String entityType, 
                                    Integer entityId, String description) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        return log;
    }
}

