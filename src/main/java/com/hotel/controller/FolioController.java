package com.hotel.controller;

import com.hotel.dto.AddLineItemRequest;
import com.hotel.dto.PaymentRequest;
import com.hotel.model.Folio;
import com.hotel.model.FolioLineItem;
import com.hotel.model.Payment;
import com.hotel.service.FolioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/folios")
public class FolioController {
    private final FolioService folioService;

    public FolioController(FolioService folioService) {
        this.folioService = folioService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Folio> getFolio(@PathVariable Integer id) {
        Folio folio = folioService.getFolio(id);
        return ResponseEntity.ok(folio);
    }

    @PostMapping("/{id}/line-items")
    public ResponseEntity<FolioLineItem> addLineItem(@PathVariable Integer id, 
                                                     @Valid @RequestBody AddLineItemRequest request) {
        FolioLineItem item = folioService.addLineItem(id, request);
        return ResponseEntity.ok(item);
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<Payment> recordPayment(@PathVariable Integer id, 
                                                 @Valid @RequestBody PaymentRequest request) {
        Payment payment = folioService.recordPayment(id, request);
        return ResponseEntity.ok(payment);
    }
}

