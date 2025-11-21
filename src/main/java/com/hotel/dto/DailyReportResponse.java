package com.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyReportResponse {
    private LocalDate date;
    private Integer totalRooms;
    private Integer occupied;
    private Double occupancyPct;
    private BigDecimal totalCashRevenue;
    private BigDecimal adr; // Average Daily Rate

    public DailyReportResponse() {}

    public DailyReportResponse(LocalDate date, Integer totalRooms, Integer occupied, 
                              Double occupancyPct, BigDecimal totalCashRevenue, BigDecimal adr) {
        this.date = date;
        this.totalRooms = totalRooms;
        this.occupied = occupied;
        this.occupancyPct = occupancyPct;
        this.totalCashRevenue = totalCashRevenue;
        this.adr = adr;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }

    public Integer getOccupied() {
        return occupied;
    }

    public void setOccupied(Integer occupied) {
        this.occupied = occupied;
    }

    public Double getOccupancyPct() {
        return occupancyPct;
    }

    public void setOccupancyPct(Double occupancyPct) {
        this.occupancyPct = occupancyPct;
    }

    public BigDecimal getTotalCashRevenue() {
        return totalCashRevenue;
    }

    public void setTotalCashRevenue(BigDecimal totalCashRevenue) {
        this.totalCashRevenue = totalCashRevenue;
    }

    public BigDecimal getAdr() {
        return adr;
    }

    public void setAdr(BigDecimal adr) {
        this.adr = adr;
    }
}

