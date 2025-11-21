package com.hotel.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class FolioLineItem {
    private Integer id;
    private Integer folioId;
    private String type;
    private String description;
    private BigDecimal amount;
    private OffsetDateTime postedAt;

    public FolioLineItem() {}

    public FolioLineItem(Integer id, Integer folioId, String type, String description, 
                         BigDecimal amount, OffsetDateTime postedAt) {
        this.id = id;
        this.folioId = folioId;
        this.type = type;
        this.description = description;
        this.amount = amount;
        this.postedAt = postedAt;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public OffsetDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(OffsetDateTime postedAt) {
        this.postedAt = postedAt;
    }
}

