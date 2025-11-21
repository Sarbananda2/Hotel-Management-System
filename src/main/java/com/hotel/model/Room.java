package com.hotel.model;

public class Room {
    private Integer id;
    private String roomNumber;
    private Integer roomTypeId;
    private String status;
    private Integer currentReservationId;
    private RoomType roomType; // For joined queries

    public Room() {}

    public Room(Integer id, String roomNumber, Integer roomTypeId, String status, Integer currentReservationId) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.roomTypeId = roomTypeId;
        this.status = status;
        this.currentReservationId = currentReservationId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Integer roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCurrentReservationId() {
        return currentReservationId;
    }

    public void setCurrentReservationId(Integer currentReservationId) {
        this.currentReservationId = currentReservationId;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
}

