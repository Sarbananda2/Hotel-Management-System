package com.hotel.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateRoomStatusRequest {
    @NotBlank(message = "Status is required")
    private String status;

    public UpdateRoomStatusRequest() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

