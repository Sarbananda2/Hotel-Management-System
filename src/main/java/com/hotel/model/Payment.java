package com.hotel.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Payment {
    private Integer id;
    private Integer folioId;
    private BigDecimal amount;
    private String method;
    private String reference;
    private OffsetDateTime createdAt;

    public Payment() {}

    public Payment(Integer id, Integer folioId, BigDecimal amount, String method, 
                  String reference, OffsetDateTime createdAt) {
        this.id = id;
        this.folioId = folioId;
        this.amount = amount;
        this.method = method;
        this.reference = reference;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFolioId() {
        return folioId;
    }

    public void setFolioId(Integer folioId) {
        this.folioId = folioId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

