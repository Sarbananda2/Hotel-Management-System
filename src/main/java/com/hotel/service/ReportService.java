package com.hotel.service;

import com.hotel.dto.DailyReportResponse;
import com.hotel.model.Room;
import com.hotel.repository.PaymentRepository;
import com.hotel.repository.RoomRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {
    private final RoomRepository roomRepository;
    private final PaymentRepository paymentRepository;
    private final JdbcTemplate jdbcTemplate;

    public ReportService(RoomRepository roomRepository, PaymentRepository paymentRepository, JdbcTemplate jdbcTemplate) {
        this.roomRepository = roomRepository;
        this.paymentRepository = paymentRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public DailyReportResponse getDailyReport(LocalDate date) {
        // Total rooms
        List<Room> allRooms = roomRepository.findAll();
        int totalRooms = allRooms.size();

        // Occupied rooms on the given date
        String occupiedSql = "SELECT COUNT(DISTINCT s.room_id) FROM stays s " +
                            "WHERE s.actual_checkin::date <= ? " +
                            "AND (s.actual_checkout IS NULL OR s.actual_checkout::date > ?)";
        Integer occupied = jdbcTemplate.queryForObject(occupiedSql, Integer.class, date, date);
        if (occupied == null) {
            occupied = 0;
        }

        // Occupancy percentage
        double occupancyPct = totalRooms > 0 ? (occupied.doubleValue() / totalRooms) * 100.0 : 0.0;

        // Total cash revenue for the date
        BigDecimal totalCashRevenue = paymentRepository.sumCashPaymentsByDate(date);

        // ADR (Average Daily Rate) - average revenue per occupied room
        BigDecimal adr = BigDecimal.ZERO;
        if (occupied > 0) {
            adr = totalCashRevenue.divide(BigDecimal.valueOf(occupied), 2, RoundingMode.HALF_UP);
        }

        return new DailyReportResponse(date, totalRooms, occupied, occupancyPct, totalCashRevenue, adr);
    }
}

