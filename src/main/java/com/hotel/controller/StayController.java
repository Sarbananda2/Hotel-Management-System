package com.hotel.controller;

import com.hotel.dto.CheckInRequest;
import com.hotel.model.Stay;
import com.hotel.service.StayService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stays")
public class StayController {
    private final StayService stayService;

    public StayController(StayService stayService) {
        this.stayService = stayService;
    }

    @PostMapping("/checkin")
    public ResponseEntity<Stay> checkIn(@Valid @RequestBody CheckInRequest request) {
        Stay stay = stayService.checkIn(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(stay);
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<Stay> checkOut(@PathVariable Integer id) {
        Stay stay = stayService.checkOut(id);
        return ResponseEntity.ok(stay);
    }
}

