package com.hotel.model;

import java.time.OffsetDateTime;
import java.util.List;

public class Folio {
    private Integer id;
    private Integer stayId;
    private Integer reservationId;
    private String currency;
    private OffsetDateTime createdAt;
    private List<FolioLineItem> lineItems;
    private List<Payment> payments;

    public Folio() {}

    public Folio(Integer id, Integer stayId, Integer reservationId, String currency, OffsetDateTime createdAt) {
        this.id = id;
        this.stayId = stayId;
        this.reservationId = reservationId;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStayId() {
        return stayId;
    }

    public void setStayId(Integer stayId) {
        this.stayId = stayId;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<FolioLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<FolioLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}

