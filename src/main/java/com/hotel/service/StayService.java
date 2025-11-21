package com.hotel.service;

import com.hotel.dto.CheckInRequest;
import com.hotel.model.*;
import com.hotel.repository.*;
import com.hotel.security.RoleChecker;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StayService {
    private final StayRepository stayRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final FolioRepository folioRepository;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final RoleChecker roleChecker;

    public StayService(StayRepository stayRepository, ReservationRepository reservationRepository,
                      RoomRepository roomRepository, FolioRepository folioRepository,
                      AuditLogRepository auditLogRepository, UserRepository userRepository,
                      RoleChecker roleChecker) {
        this.stayRepository = stayRepository;
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.folioRepository = folioRepository;
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
        this.roleChecker = roleChecker;
    }

    @Transactional
    public Stay checkIn(CheckInRequest request) {
        // Validate reservation
        Optional<Reservation> reservationOpt = reservationRepository.findById(request.getReservationId());
        if (reservationOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found");
        }

        Reservation reservation = reservationOpt.get();
        if (!"BOOKED".equals(reservation.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation must be BOOKED to check in");
        }

        // Validate room
        Optional<Room> roomOpt = roomRepository.findById(request.getRoomId());
        if (roomOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }

        Room room = roomOpt.get();
        if (!room.getRoomTypeId().equals(reservation.getRoomTypeId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room type does not match reservation");
        }

        if (!"VACANT".equals(room.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room is not available");
        }

        // Check if stay already exists
        Optional<Stay> existingStay = stayRepository.findByReservationId(request.getReservationId());
        if (existingStay.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stay already exists for this reservation");
        }

        // Create folio
        Folio folio = new Folio();
        folio.setStayId(null); // Will be set after stay creation
        folio.setReservationId(request.getReservationId());
        folio.setCurrency("INR");
        folio = folioRepository.create(folio);

        // Create stay
        Stay stay = new Stay();
        stay.setReservationId(request.getReservationId());
        stay.setRoomId(request.getRoomId());
        stay.setActualCheckin(OffsetDateTime.now());
        stay.setFolioId(folio.getId());
        stay = stayRepository.create(stay);

        // Update folio with stay ID
        // Note: This is a circular reference, but we'll handle it
        folio.setStayId(stay.getId());

        // Update room status
        roomRepository.updateStatusAndReservation(request.getRoomId(), "OCCUPIED", request.getReservationId());

        // Update reservation status
        reservation.setStatus("CHECKED_IN");
        reservationRepository.update(reservation);

        // Audit log
        Integer userId = getCurrentUserId();
        auditLogRepository.create(createAuditLog(userId, "CHECK_IN", "STAY", stay.getId(), 
            "Checked in reservation " + request.getReservationId() + " to room " + request.getRoomId()));

        return stay;
    }

    @Transactional
    public Stay checkOut(Integer stayId) {
        Optional<Stay> stayOpt = stayRepository.findById(stayId);
        if (stayOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stay not found");
        }

        Stay stay = stayOpt.get();
        if (stay.getActualCheckout() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stay already checked out");
        }

        // Update stay
        stay.setActualCheckout(OffsetDateTime.now());
        stayRepository.updateCheckout(stayId, stay.getActualCheckout());

        // Update room status
        roomRepository.updateStatus(stay.getRoomId(), "VACANT");

        // Update reservation status
        Optional<Reservation> reservationOpt = reservationRepository.findById(stay.getReservationId());
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            reservation.setStatus("CHECKED_OUT");
            reservationRepository.update(reservation);
        }

        // Audit log
        Integer userId = getCurrentUserId();
        auditLogRepository.create(createAuditLog(userId, "CHECK_OUT", "STAY", stayId, 
            "Checked out stay " + stayId));

        return stay;
    }

    private Integer getCurrentUserId() {
        String email = roleChecker.getCurrentUserEmail();
        if (email != null) {
            return userRepository.findByEmail(email)
                    .map(User::getId)
                    .orElse(null);
        }
        return null;
    }

    public List<Stay> findActiveStays() {
        return stayRepository.findActiveStays();
    }

    private AuditLog createAuditLog(Integer userId, String action, String entityType, 
                                    Integer entityId, String description) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        return log;
    }
}

