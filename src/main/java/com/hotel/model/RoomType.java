package com.hotel.model;

import java.math.BigDecimal;

public class RoomType {
    private Integer id;
    private String name;
    private Integer capacity;
    private BigDecimal baseRate;
    private String description;

    public RoomType() {}

    public RoomType(Integer id, String name, Integer capacity, BigDecimal baseRate, String description) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.baseRate = baseRate;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(BigDecimal baseRate) {
        this.baseRate = baseRate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

