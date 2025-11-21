package com.hotel.model;

import java.time.OffsetDateTime;

public class Stay {
    private Integer id;
    private Integer reservationId;
    private Integer roomId;
    private OffsetDateTime actualCheckin;
    private OffsetDateTime actualCheckout;
    private Integer folioId;
    private Reservation reservation; // For joined queries
    private Room room; // For joined queries

    public Stay() {}

    public Stay(Integer id, Integer reservationId, Integer roomId, OffsetDateTime actualCheckin, 
               OffsetDateTime actualCheckout, Integer folioId) {
        this.id = id;
        this.reservationId = reservationId;
        this.roomId = roomId;
        this.actualCheckin = actualCheckin;
        this.actualCheckout = actualCheckout;
        this.folioId = folioId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public OffsetDateTime getActualCheckin() {
        return actualCheckin;
    }

    public void setActualCheckin(OffsetDateTime actualCheckin) {
        this.actualCheckin = actualCheckin;
    }

    public OffsetDateTime getActualCheckout() {
        return actualCheckout;
    }

    public void setActualCheckout(OffsetDateTime actualCheckout) {
        this.actualCheckout = actualCheckout;
    }

    public Integer getFolioId() {
        return folioId;
    }

    public void setFolioId(Integer folioId) {
        this.folioId = folioId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}

