package com.hotel.dto;

import jakarta.validation.constraints.Positive;

public class UpdateHousekeepingTaskRequest {
    private String status;
    
    @Positive(message = "Assigned to ID must be positive")
    private Integer assignedTo;

    private String notes;

    public UpdateHousekeepingTaskRequest() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Integer assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

