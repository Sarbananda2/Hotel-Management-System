package com.hotel.integration;

import com.hotel.HotelManagementApplication;
import com.hotel.dto.*;
import com.hotel.model.*;
import com.hotel.repository.*;
import com.hotel.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = HotelManagementApplication.class)
@Testcontainers
@Transactional
class ReservationFlowIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private StayService stayService;
    
    @Autowired
    private FolioService folioService;
    
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private StayRepository stayRepository;
    
    @Autowired
    private FolioRepository folioRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;

    private RoomType roomType;
    private Room room;

    @BeforeEach
    void setUp() {
        // Create room type
        RoomType rt = new RoomType();
        rt.setName("Standard");
        rt.setCapacity(2);
        rt.setBaseRate(new BigDecimal("3000.00"));
        roomType = roomTypeRepository.create(rt);

        // Create room
        Room r = new Room();
        r.setRoomNumber("101");
        r.setRoomTypeId(roomType.getId());
        r.setStatus("VACANT");
        room = roomRepository.create(r);
    }

    @Test
    void testReservationToPaymentFlow() {
        // Create reservation
        CreateReservationRequest reservationRequest = new CreateReservationRequest();
        reservationRequest.setGuestName("Test Guest");
        reservationRequest.setGuestEmail("guest@example.com");
        reservationRequest.setPhone("+919900112233");
        reservationRequest.setRoomTypeId(roomType.getId());
        reservationRequest.setCheckInDate(LocalDate.now().plusDays(1));
        reservationRequest.setCheckOutDate(LocalDate.now().plusDays(3));

        Reservation reservation = reservationService.create(reservationRequest);
        assertNotNull(reservation);
        assertNotNull(reservation.getId());
        assertEquals("BOOKED", reservation.getStatus());

        // Check in
        CheckInRequest checkInRequest = new CheckInRequest();
        checkInRequest.setReservationId(reservation.getId());
        checkInRequest.setRoomId(room.getId());

        Stay stay = stayService.checkIn(checkInRequest);
        assertNotNull(stay);
        assertNotNull(stay.getId());
        assertNotNull(stay.getFolioId());

        // Get folio
        Folio folio = folioService.getFolio(stay.getFolioId());
        assertNotNull(folio);

        // Add line item
        AddLineItemRequest lineItemRequest = new AddLineItemRequest();
        lineItemRequest.setType("ROOM_CHARGE");
        lineItemRequest.setDescription("Room charge");
        lineItemRequest.setAmount(new BigDecimal("3000.00"));

        FolioLineItem lineItem = folioService.addLineItem(folio.getId(), lineItemRequest);
        assertNotNull(lineItem);
        assertNotNull(lineItem.getId());

        // Record payment
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(new BigDecimal("3000.00"));
        paymentRequest.setMethod("CASH");
        paymentRequest.setReference("Receipt #123");

        Payment payment = folioService.recordPayment(folio.getId(), paymentRequest);
        assertNotNull(payment);
        assertNotNull(payment.getId());
        assertEquals("CASH", payment.getMethod());
    }
}

