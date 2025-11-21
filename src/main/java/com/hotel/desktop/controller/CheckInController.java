package com.hotel.desktop.controller;

import com.hotel.dto.CheckInRequest;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.service.ReservationService;
import com.hotel.service.RoomService;
import com.hotel.service.StayService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller("desktopCheckInController")
public class CheckInController implements Initializable {
    @Autowired
    private StayService stayService;
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private RoomService roomService;

    @FXML
    private ComboBox<Reservation> reservationCombo;

    @FXML
    private ComboBox<Room> roomCombo;

    @FXML
    private Button checkInButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadBookedReservations();
        loadVacantRooms();
        
        checkInButton.setOnAction(e -> handleCheckIn());
    }

    private void loadBookedReservations() {
        List<Reservation> reservations = reservationService.findAll(null, null, "BOOKED");
        reservationCombo.setItems(FXCollections.observableArrayList(reservations));
    }

    private void loadVacantRooms() {
        List<Room> rooms = roomService.findAll().stream()
                .filter(r -> "VACANT".equals(r.getStatus()))
                .collect(Collectors.toList());
        roomCombo.setItems(FXCollections.observableArrayList(rooms));
    }

    private void handleCheckIn() {
        Reservation reservation = reservationCombo.getValue();
        Room room = roomCombo.getValue();
        
        if (reservation == null || room == null) {
            showError("Error", "Missing Information", "Please select both reservation and room");
            return;
        }

        try {
            CheckInRequest request = new CheckInRequest();
            request.setReservationId(reservation.getId());
            request.setRoomId(room.getId());
            
            stayService.checkIn(request);
            showSuccess("Check-In Successful", "Guest checked in successfully!");
            loadBookedReservations();
            loadVacantRooms();
        } catch (Exception e) {
            showError("Error", "Check-in Failed", e.getMessage());
        }
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

