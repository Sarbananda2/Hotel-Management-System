package com.hotel.controller;

import com.hotel.dto.CreateReservationRequest;
import com.hotel.model.Reservation;
import com.hotel.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<Reservation> create(@Valid @RequestBody CreateReservationRequest request) {
        Reservation reservation = reservationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> findAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String status) {
        List<Reservation> reservations = reservationService.findAll(from, to, status);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> findById(@PathVariable Integer id) {
        Optional<Reservation> reservation = reservationService.findById(id);
        return reservation.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Reservation> update(@PathVariable Integer id, 
                                              @Valid @RequestBody CreateReservationRequest request) {
        Reservation reservation = reservationService.update(id, request);
        return ResponseEntity.ok(reservation);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Integer id) {
        reservationService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}

