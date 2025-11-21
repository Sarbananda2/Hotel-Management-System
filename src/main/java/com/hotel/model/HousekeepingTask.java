package com.hotel.model;

import java.time.OffsetDateTime;

public class HousekeepingTask {
    private Integer id;
    private Integer roomId;
    private String status;
    private Integer assignedTo;
    private String notes;
    private OffsetDateTime createdAt;
    private Room room; // For joined queries
    private User assignedUser; // For joined queries

    public HousekeepingTask() {}

    public HousekeepingTask(Integer id, Integer roomId, String status, Integer assignedTo, 
                           String notes, OffsetDateTime createdAt) {
        this.id = id;
        this.roomId = roomId;
        this.status = status;
        this.assignedTo = assignedTo;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }
}

