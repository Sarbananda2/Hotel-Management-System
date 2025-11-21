package com.hotel.controller;

import com.hotel.dto.CreateRoomRequest;
import com.hotel.dto.CreateRoomTypeRequest;
import com.hotel.dto.UpdateRoomStatusRequest;
import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<Room>> getRooms() {
        List<Room> rooms = roomService.findAll();
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/room-types")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomType> createRoomType(@Valid @RequestBody CreateRoomTypeRequest request) {
        RoomType roomType = roomService.createRoomType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomType);
    }

    @PostMapping("/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Room> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        Room room = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    @PatchMapping("/rooms/{id}/status")
    public ResponseEntity<Room> updateRoomStatus(@PathVariable Integer id, 
                                               @Valid @RequestBody UpdateRoomStatusRequest request) {
        Room room = roomService.updateRoomStatus(id, request);
        return ResponseEntity.ok(room);
    }
}

