package com.hotel.service;

import com.hotel.dto.CreateHousekeepingTaskRequest;
import com.hotel.dto.UpdateHousekeepingTaskRequest;
import com.hotel.model.HousekeepingTask;
import com.hotel.model.Room;
import com.hotel.repository.HousekeepingTaskRepository;
import com.hotel.repository.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class HousekeepingService {
    private final HousekeepingTaskRepository taskRepository;
    private final RoomRepository roomRepository;

    public HousekeepingService(HousekeepingTaskRepository taskRepository, RoomRepository roomRepository) {
        this.taskRepository = taskRepository;
        this.roomRepository = roomRepository;
    }

    public List<HousekeepingTask> findAll(String status) {
        if (status != null) {
            return taskRepository.findByStatus(status);
        }
        return taskRepository.findAll();
    }

    @Transactional
    public HousekeepingTask create(CreateHousekeepingTaskRequest request) {
        // Validate room exists
        Optional<Room> roomOpt = roomRepository.findById(request.getRoomId());
        if (roomOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }

        HousekeepingTask task = new HousekeepingTask();
        task.setRoomId(request.getRoomId());
        task.setStatus("OPEN");
        task.setNotes(request.getNotes());

        return taskRepository.create(task);
    }

    @Transactional
    public HousekeepingTask update(Integer id, UpdateHousekeepingTaskRequest request) {
        Optional<HousekeepingTask> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }

        HousekeepingTask task = taskOpt.get();
        
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getAssignedTo() != null) {
            task.setAssignedTo(request.getAssignedTo());
        }
        if (request.getNotes() != null) {
            task.setNotes(request.getNotes());
        }

        taskRepository.update(task);
        return task;
    }

    @Transactional
    public void updateRoomStatus(Integer roomId, String status) {
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }

        roomRepository.updateStatus(roomId, status);
    }
}

