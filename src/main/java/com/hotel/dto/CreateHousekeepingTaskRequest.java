package com.hotel.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateHousekeepingTaskRequest {
    @NotNull(message = "Room ID is required")
    @Positive(message = "Room ID must be positive")
    private Integer roomId;

    private String notes;

    public CreateHousekeepingTaskRequest() {}

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

