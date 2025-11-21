package com.hotel.repository;

import com.hotel.model.HousekeepingTask;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class HousekeepingTaskRepository {
    private final JdbcTemplate jdbcTemplate;

    public HousekeepingTaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<HousekeepingTask> TASK_ROW_MAPPER = new RowMapper<HousekeepingTask>() {
        @Override
        public HousekeepingTask mapRow(ResultSet rs, int rowNum) throws SQLException {
            HousekeepingTask task = new HousekeepingTask();
            task.setId(rs.getInt("id"));
            Integer roomId = rs.getObject("room_id", Integer.class);
            task.setRoomId(roomId);
            task.setStatus(rs.getString("status"));
            Integer assignedTo = rs.getObject("assigned_to", Integer.class);
            task.setAssignedTo(assignedTo);
            task.setNotes(rs.getString("notes"));
            task.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
            return task;
        }
    };

    public HousekeepingTask create(HousekeepingTask task) {
        String sql = "INSERT INTO housekeeping_tasks (room_id, status, assigned_to, notes) VALUES (?, ?, ?, ?) RETURNING id, created_at";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            task.setId(rs.getInt("id"));
            task.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
            return task;
        }, task.getRoomId(), task.getStatus(), task.getAssignedTo(), task.getNotes());
    }

    public List<HousekeepingTask> findAll() {
        String sql = "SELECT id, room_id, status, assigned_to, notes, created_at FROM housekeeping_tasks ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, TASK_ROW_MAPPER);
    }

    public List<HousekeepingTask> findByStatus(String status) {
        String sql = "SELECT id, room_id, status, assigned_to, notes, created_at FROM housekeeping_tasks WHERE status = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, TASK_ROW_MAPPER, status);
    }

    public Optional<HousekeepingTask> findById(Integer id) {
        String sql = "SELECT id, room_id, status, assigned_to, notes, created_at FROM housekeeping_tasks WHERE id = ?";
        try {
            HousekeepingTask task = jdbcTemplate.queryForObject(sql, TASK_ROW_MAPPER, id);
            return Optional.ofNullable(task);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void update(HousekeepingTask task) {
        String sql = "UPDATE housekeeping_tasks SET room_id = ?, status = ?, assigned_to = ?, notes = ? WHERE id = ?";
        jdbcTemplate.update(sql, task.getRoomId(), task.getStatus(), task.getAssignedTo(), task.getNotes(), task.getId());
    }
}

