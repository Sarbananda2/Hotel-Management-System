package com.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateRoomRequest {
    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "Room type ID is required")
    @Positive(message = "Room type ID must be positive")
    private Integer roomTypeId;

    public CreateRoomRequest() {}

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
}

