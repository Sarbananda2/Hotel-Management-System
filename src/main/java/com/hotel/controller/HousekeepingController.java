package com.hotel.controller;

import com.hotel.dto.CreateHousekeepingTaskRequest;
import com.hotel.dto.UpdateHousekeepingTaskRequest;
import com.hotel.model.HousekeepingTask;
import com.hotel.service.HousekeepingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/housekeeping")
public class HousekeepingController {
    private final HousekeepingService housekeepingService;

    public HousekeepingController(HousekeepingService housekeepingService) {
        this.housekeepingService = housekeepingService;
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<HousekeepingTask>> getTasks(@RequestParam(required = false) String status) {
        List<HousekeepingTask> tasks = housekeepingService.findAll(status);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/tasks")
    public ResponseEntity<HousekeepingTask> createTask(@Valid @RequestBody CreateHousekeepingTaskRequest request) {
        HousekeepingTask task = housekeepingService.create(request);
        return ResponseEntity.ok(task);
    }

    @PatchMapping("/tasks/{id}")
    public ResponseEntity<HousekeepingTask> updateTask(@PathVariable Integer id, 
                                                       @Valid @RequestBody UpdateHousekeepingTaskRequest request) {
        HousekeepingTask task = housekeepingService.update(id, request);
        return ResponseEntity.ok(task);
    }
}

