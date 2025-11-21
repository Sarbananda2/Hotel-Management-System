package com.hotel.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class Reservation {
    private Integer id;
    private String guestName;
    private String guestEmail;
    private String phone;
    private Integer roomTypeId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;
    private Integer createdBy;
    private OffsetDateTime createdAt;
    private RoomType roomType; // For joined queries

    public Reservation() {}

    public Reservation(Integer id, String guestName, String guestEmail, String phone, 
                      Integer roomTypeId, LocalDate checkInDate, LocalDate checkOutDate, 
                      String status, Integer createdBy, OffsetDateTime createdAt) {
        this.id = id;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.phone = phone;
        this.roomTypeId = roomTypeId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Integer roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
}

