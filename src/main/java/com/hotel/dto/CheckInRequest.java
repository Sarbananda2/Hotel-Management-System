package com.hotel.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CheckInRequest {
    @NotNull(message = "Reservation ID is required")
    @Positive(message = "Reservation ID must be positive")
    private Integer reservationId;

    @NotNull(message = "Room ID is required")
    @Positive(message = "Room ID must be positive")
    private Integer roomId;

    public CheckInRequest() {}

    public CheckInRequest(Integer reservationId, Integer roomId) {
        this.reservationId = reservationId;
        this.roomId = roomId;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }
}

