package com.hotel.service;

import com.hotel.dto.CreateRoomRequest;
import com.hotel.dto.CreateRoomTypeRequest;
import com.hotel.dto.UpdateRoomStatusRequest;
import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.RoomTypeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    public RoomService(RoomRepository roomRepository, RoomTypeRepository roomTypeRepository) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public List<RoomType> findAllRoomTypes() {
        return roomTypeRepository.findAll();
    }

    @Transactional
    public RoomType createRoomType(CreateRoomTypeRequest request) {
        RoomType roomType = new RoomType();
        roomType.setName(request.getName());
        roomType.setCapacity(request.getCapacity());
        roomType.setBaseRate(request.getBaseRate());
        roomType.setDescription(request.getDescription());

        return roomTypeRepository.create(roomType);
    }

    @Transactional
    public Room createRoom(CreateRoomRequest request) {
        // Validate room type exists
        Optional<RoomType> roomTypeOpt = roomTypeRepository.findById(request.getRoomTypeId());
        if (roomTypeOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room type not found");
        }

        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomTypeId(request.getRoomTypeId());
        room.setStatus("VACANT");

        return roomRepository.create(room);
    }

    @Transactional
    public Room updateRoomStatus(Integer id, UpdateRoomStatusRequest request) {
        Optional<Room> roomOpt = roomRepository.findById(id);
        if (roomOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }

        roomRepository.updateStatus(id, request.getStatus());
        return roomRepository.findById(id).orElseThrow();
    }
}

