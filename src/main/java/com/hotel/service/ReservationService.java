package com.hotel.service;

import com.hotel.dto.CreateReservationRequest;
import com.hotel.model.Reservation;
import com.hotel.model.RoomType;
import com.hotel.repository.ReservationRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.RoomTypeRepository;
import com.hotel.repository.AuditLogRepository;
import com.hotel.security.RoleChecker;
import com.hotel.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final AuditLogRepository auditLogRepository;
    private final RoleChecker roleChecker;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository, 
                             RoomTypeRepository roomTypeRepository,
                             AuditLogRepository auditLogRepository,
                             RoleChecker roleChecker,
                             UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.auditLogRepository = auditLogRepository;
        this.roleChecker = roleChecker;
        this.userRepository = userRepository;
    }

    @Transactional
    public Reservation create(CreateReservationRequest request) {
        // Validate dates
        if (request.getCheckInDate().isAfter(request.getCheckOutDate()) || 
            request.getCheckInDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date range");
        }

        // Check room type exists
        Optional<RoomType> roomTypeOpt = roomTypeRepository.findById(request.getRoomTypeId());
        if (roomTypeOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room type not found");
        }

        // Check availability with SELECT FOR UPDATE for concurrency
        int overlappingReservations = reservationRepository.countOverlappingReservations(
            request.getRoomTypeId(), request.getCheckInDate(), request.getCheckOutDate(), null);
        int availableRooms = reservationRepository.countAvailableRooms(request.getRoomTypeId());

        if (overlappingReservations >= availableRooms) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No availability for selected dates");
        }

        // Get current user ID from security context
        Integer userId = getCurrentUserId();

        Reservation reservation = new Reservation();
        reservation.setGuestName(request.getGuestName());
        reservation.setGuestEmail(request.getGuestEmail());
        reservation.setPhone(request.getPhone());
        reservation.setRoomTypeId(request.getRoomTypeId());
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setStatus("BOOKED");
        reservation.setCreatedBy(userId);

        Reservation created = reservationRepository.create(reservation);

        // Audit log
        auditLogRepository.create(createAuditLog(userId, "CREATE_RESERVATION", "RESERVATION", created.getId(), 
            "Created reservation for " + request.getGuestName()));

        return created;
    }

    public List<Reservation> findAll(LocalDate from, LocalDate to, String status) {
        if (from != null && to != null) {
            return reservationRepository.findByDateRange(from, to, status != null ? status : "BOOKED");
        }
        return reservationRepository.findAll(status);
    }

    public Optional<Reservation> findById(Integer id) {
        return reservationRepository.findById(id);
    }

    @Transactional
    public Reservation update(Integer id, CreateReservationRequest request) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(id);
        if (reservationOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found");
        }

        Reservation reservation = reservationOpt.get();
        if (!"BOOKED".equals(reservation.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only modify BOOKED reservations");
        }

        // Check availability if dates or room type changed
        if (!reservation.getCheckInDate().equals(request.getCheckInDate()) ||
            !reservation.getCheckOutDate().equals(request.getCheckOutDate()) ||
            !reservation.getRoomTypeId().equals(request.getRoomTypeId())) {
            
            int overlappingReservations = reservationRepository.countOverlappingReservations(
                request.getRoomTypeId(), request.getCheckInDate(), request.getCheckOutDate(), id);
            int availableRooms = reservationRepository.countAvailableRooms(request.getRoomTypeId());

            if (overlappingReservations >= availableRooms) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "No availability for selected dates");
            }
        }

        reservation.setGuestName(request.getGuestName());
        reservation.setGuestEmail(request.getGuestEmail());
        reservation.setPhone(request.getPhone());
        reservation.setRoomTypeId(request.getRoomTypeId());
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());

        reservationRepository.update(reservation);

        Integer userId = getCurrentUserId();
        auditLogRepository.create(createAuditLog(userId, "UPDATE_RESERVATION", "RESERVATION", id, 
            "Updated reservation"));

        return reservation;
    }

    @Transactional
    public void cancel(Integer id) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(id);
        if (reservationOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found");
        }

        Reservation reservation = reservationOpt.get();
        reservation.setStatus("CANCELLED");
        reservationRepository.update(reservation);

        Integer userId = getCurrentUserId();
        auditLogRepository.create(createAuditLog(userId, "CANCEL_RESERVATION", "RESERVATION", id, 
            "Cancelled reservation"));
    }

    private Integer getCurrentUserId() {
        String email = roleChecker.getCurrentUserEmail();
        if (email != null) {
            return userRepository.findByEmail(email)
                    .map(com.hotel.model.User::getId)
                    .orElse(null);
        }
        return null;
    }

    private com.hotel.model.AuditLog createAuditLog(Integer userId, String action, String entityType, 
                                                    Integer entityId, String description) {
        com.hotel.model.AuditLog log = new com.hotel.model.AuditLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        // Meta can be set as JSON if needed
        return log;
    }
}

