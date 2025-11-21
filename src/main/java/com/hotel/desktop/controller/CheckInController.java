package com.hotel.desktop.controller;

import com.hotel.dto.CheckInRequest;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.model.Stay;
import com.hotel.service.ReservationService;
import com.hotel.service.RoomService;
import com.hotel.service.StayService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    @FXML
    private Button clearButton;
    
    // Reservation Details Panel
    @FXML
    private VBox reservationDetailsPanel;
    
    @FXML
    private Label guestNameLabel;
    
    @FXML
    private Label guestEmailLabel;
    
    @FXML
    private Label phoneLabel;
    
    @FXML
    private Label roomTypeLabel;
    
    @FXML
    private Label checkInDateLabel;
    
    @FXML
    private Label checkOutDateLabel;
    
    @FXML
    private Label nightsLabel;
    
    @FXML
    private Label reservationStatusLabel;
    
    // Room Details Panel
    @FXML
    private VBox roomDetailsPanel;
    
    @FXML
    private Label roomNumberLabel;
    
    @FXML
    private Label roomTypeNameLabel;
    
    @FXML
    private Label roomStatusLabel;
    
    @FXML
    private Label validationLabel;
    
    // Cache for room types
    private Map<Integer, RoomType> roomTypeCache = new HashMap<>();
    
    // All vacant rooms (for filtering)
    private List<Room> allVacantRooms;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadRoomTypeCache();
        setupComboBoxes();
        setupListeners();
        loadBookedReservations();
        loadAllVacantRooms();
        
        checkInButton.setOnAction(e -> handleCheckIn());
        clearButton.setOnAction(e -> clearForm());
        
        // Initially disable check-in button
        checkInButton.setDisable(true);
    }
    
    private void loadRoomTypeCache() {
        List<RoomType> roomTypes = roomService.findAllRoomTypes();
        for (RoomType rt : roomTypes) {
            roomTypeCache.put(rt.getId(), rt);
        }
    }
    
    private void setupListeners() {
        // Listen for reservation selection changes
        reservationCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateReservationDetails(newVal);
                filterRoomsByReservationType(newVal);
                validateCheckIn();
            } else {
                hideReservationDetails();
                loadAllVacantRooms();
                validateCheckIn();
            }
        });
        
        // Listen for room selection changes
        roomCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateRoomDetails(newVal);
                validateCheckIn();
            } else {
                hideRoomDetails();
                validateCheckIn();
            }
        });
    }

    private void setupComboBoxes() {
        // Set cell factory for reservation combo to display user-friendly information
        reservationCombo.setCellFactory(param -> new ListCell<Reservation>() {
            @Override
            protected void updateItem(Reservation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String displayText = String.format("Reservation #%d - %s (%s)", 
                        item.getId(), 
                        item.getGuestName() != null ? item.getGuestName() : "N/A",
                        item.getCheckInDate() != null ? item.getCheckInDate().toString() : "N/A");
                    setText(displayText);
                }
            }
        });
        
        // Set button cell for reservation combo
        reservationCombo.setButtonCell(new ListCell<Reservation>() {
            @Override
            protected void updateItem(Reservation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String displayText = String.format("Reservation #%d - %s (%s)", 
                        item.getId(), 
                        item.getGuestName() != null ? item.getGuestName() : "N/A",
                        item.getCheckInDate() != null ? item.getCheckInDate().toString() : "N/A");
                    setText(displayText);
                }
            }
        });
        
        // Set cell factory for room combo to display room number and type
        roomCombo.setCellFactory(param -> new ListCell<Room>() {
            @Override
            protected void updateItem(Room item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String displayText;
                    if (item.getRoomType() != null && item.getRoomType().getName() != null) {
                        displayText = String.format("%s - %s", 
                            item.getRoomNumber(), 
                            item.getRoomType().getName());
                    } else {
                        displayText = item.getRoomNumber();
                    }
                    setText(displayText);
                }
            }
        });
        
        // Set button cell for room combo
        roomCombo.setButtonCell(new ListCell<Room>() {
            @Override
            protected void updateItem(Room item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String displayText;
                    if (item.getRoomType() != null && item.getRoomType().getName() != null) {
                        displayText = String.format("%s - %s", 
                            item.getRoomNumber(), 
                            item.getRoomType().getName());
                    } else {
                        displayText = item.getRoomNumber();
                    }
                    setText(displayText);
                }
            }
        });
    }

    private void loadBookedReservations() {
        List<Reservation> reservations = reservationService.findAll(null, null, "BOOKED");
        // Enrich reservations with room type information
        for (Reservation r : reservations) {
            if (r.getRoomTypeId() != null && roomTypeCache.containsKey(r.getRoomTypeId())) {
                r.setRoomType(roomTypeCache.get(r.getRoomTypeId()));
            }
        }
        reservationCombo.setItems(FXCollections.observableArrayList(reservations));
    }

    private void loadAllVacantRooms() {
        allVacantRooms = roomService.findAll().stream()
                .filter(r -> "VACANT".equals(r.getStatus()))
                .collect(Collectors.toList());
        // Enrich rooms with room type information
        for (Room r : allVacantRooms) {
            if (r.getRoomTypeId() != null && roomTypeCache.containsKey(r.getRoomTypeId())) {
                r.setRoomType(roomTypeCache.get(r.getRoomTypeId()));
            }
        }
        roomCombo.setItems(FXCollections.observableArrayList(allVacantRooms));
    }
    
    private void filterRoomsByReservationType(Reservation reservation) {
        if (reservation == null || reservation.getRoomTypeId() == null) {
            loadAllVacantRooms();
            return;
        }
        
        List<Room> filteredRooms = allVacantRooms.stream()
                .filter(r -> r.getRoomTypeId() != null && r.getRoomTypeId().equals(reservation.getRoomTypeId()))
                .collect(Collectors.toList());
        
        // Enrich with room type information
        for (Room r : filteredRooms) {
            if (r.getRoomTypeId() != null && roomTypeCache.containsKey(r.getRoomTypeId())) {
                r.setRoomType(roomTypeCache.get(r.getRoomTypeId()));
            }
        }
        
        roomCombo.setItems(FXCollections.observableArrayList(filteredRooms));
        
        // Clear room selection when filtering
        roomCombo.setValue(null);
    }
    
    private void updateReservationDetails(Reservation reservation) {
        if (reservation == null) {
            hideReservationDetails();
            return;
        }
        
        guestNameLabel.setText(reservation.getGuestName() != null ? reservation.getGuestName() : "N/A");
        guestEmailLabel.setText(reservation.getGuestEmail() != null ? reservation.getGuestEmail() : "N/A");
        phoneLabel.setText(reservation.getPhone() != null ? reservation.getPhone() : "N/A");
        
        // Room type
        RoomType roomType = reservation.getRoomType();
        if (roomType == null && reservation.getRoomTypeId() != null) {
            roomType = roomTypeCache.get(reservation.getRoomTypeId());
        }
        if (roomType != null) {
            roomTypeLabel.setText(String.format("%s (₹%s/night, Capacity: %d)", 
                roomType.getName(), 
                roomType.getBaseRate(),
                roomType.getCapacity()));
        } else {
            roomTypeLabel.setText("N/A");
        }
        
        // Dates
        if (reservation.getCheckInDate() != null) {
            checkInDateLabel.setText(reservation.getCheckInDate().toString());
        } else {
            checkInDateLabel.setText("N/A");
        }
        
        if (reservation.getCheckOutDate() != null) {
            checkOutDateLabel.setText(reservation.getCheckOutDate().toString());
        } else {
            checkOutDateLabel.setText("N/A");
        }
        
        // Calculate nights
        if (reservation.getCheckInDate() != null && reservation.getCheckOutDate() != null) {
            long nights = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
            nightsLabel.setText(String.valueOf(nights));
        } else {
            nightsLabel.setText("N/A");
        }
        
        reservationStatusLabel.setText(reservation.getStatus() != null ? reservation.getStatus() : "N/A");
        
        reservationDetailsPanel.setVisible(true);
        reservationDetailsPanel.setManaged(true);
    }
    
    private void hideReservationDetails() {
        reservationDetailsPanel.setVisible(false);
        reservationDetailsPanel.setManaged(false);
    }
    
    private void updateRoomDetails(Room room) {
        if (room == null) {
            hideRoomDetails();
            return;
        }
        
        roomNumberLabel.setText(room.getRoomNumber() != null ? room.getRoomNumber() : "N/A");
        roomStatusLabel.setText(room.getStatus() != null ? room.getStatus() : "N/A");
        
        // Room type
        RoomType roomType = room.getRoomType();
        if (roomType == null && room.getRoomTypeId() != null) {
            roomType = roomTypeCache.get(room.getRoomTypeId());
        }
        if (roomType != null) {
            roomTypeNameLabel.setText(String.format("%s (₹%s/night, Capacity: %d)", 
                roomType.getName(), 
                roomType.getBaseRate(),
                roomType.getCapacity()));
        } else {
            roomTypeNameLabel.setText("N/A");
        }
        
        // Validation indicator
        Reservation selectedReservation = reservationCombo.getValue();
        if (selectedReservation != null && room.getRoomTypeId() != null && 
            selectedReservation.getRoomTypeId() != null &&
            room.getRoomTypeId().equals(selectedReservation.getRoomTypeId())) {
            validationLabel.setText("✓ Room type matches reservation");
            validationLabel.setTextFill(Color.GREEN);
        } else if (selectedReservation != null) {
            validationLabel.setText("⚠ Room type does not match reservation");
            validationLabel.setTextFill(Color.ORANGE);
        } else {
            validationLabel.setText("");
        }
        
        roomDetailsPanel.setVisible(true);
        roomDetailsPanel.setManaged(true);
    }
    
    private void hideRoomDetails() {
        roomDetailsPanel.setVisible(false);
        roomDetailsPanel.setManaged(false);
        validationLabel.setText("");
    }
    
    private void validateCheckIn() {
        Reservation reservation = reservationCombo.getValue();
        Room room = roomCombo.getValue();
        
        boolean isValid = true;
        String validationMessage = "";
        
        if (reservation == null) {
            isValid = false;
            validationMessage = "Please select a reservation";
        } else if (!"BOOKED".equals(reservation.getStatus())) {
            isValid = false;
            validationMessage = "Reservation must be in BOOKED status";
        } else if (room == null) {
            isValid = false;
            validationMessage = "Please select a room";
        } else if (!"VACANT".equals(room.getStatus())) {
            isValid = false;
            validationMessage = "Room must be VACANT";
        } else if (reservation.getRoomTypeId() != null && room.getRoomTypeId() != null &&
                   !reservation.getRoomTypeId().equals(room.getRoomTypeId())) {
            isValid = false;
            validationMessage = "Room type does not match reservation";
        }
        
        checkInButton.setDisable(!isValid);
        
        // Update validation label if needed
        if (!isValid && room != null) {
            validationLabel.setText("✗ " + validationMessage);
            validationLabel.setTextFill(Color.RED);
        }
    }
    
    private void clearForm() {
        reservationCombo.setValue(null);
        roomCombo.setValue(null);
        hideReservationDetails();
        hideRoomDetails();
        loadAllVacantRooms();
        checkInButton.setDisable(true);
    }

    private void handleCheckIn() {
        Reservation reservation = reservationCombo.getValue();
        Room room = roomCombo.getValue();
        
        if (reservation == null || room == null) {
            showError("Error", "Missing Information", "Please select both reservation and room");
            return;
        }
        
        // Show confirmation dialog
        if (!showConfirmationDialog(reservation, room)) {
            return;
        }

        try {
            // Disable button during processing
            checkInButton.setDisable(true);
            checkInButton.setText("Processing...");
            
            CheckInRequest request = new CheckInRequest();
            request.setReservationId(reservation.getId());
            request.setRoomId(room.getId());
            
            Stay stay = stayService.checkIn(request);
            
            showSuccessDialog(stay, reservation, room);
            clearForm();
            loadBookedReservations();
            loadAllVacantRooms();
        } catch (Exception e) {
            showError("Error", "Check-in Failed", e.getMessage());
        } finally {
            checkInButton.setText("Check In");
            validateCheckIn();
        }
    }
    
    private boolean showConfirmationDialog(Reservation reservation, Room room) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Check-In");
        alert.setHeaderText("Confirm Check-In");
        
        StringBuilder content = new StringBuilder();
        content.append("Are you sure you want to check in this guest?\n\n");
        content.append("Guest: ").append(reservation.getGuestName()).append("\n");
        content.append("Reservation #: ").append(reservation.getId()).append("\n");
        content.append("Room: ").append(room.getRoomNumber()).append("\n");
        if (reservation.getCheckInDate() != null) {
            content.append("Check-in Date: ").append(reservation.getCheckInDate()).append("\n");
        }
        if (reservation.getCheckOutDate() != null) {
            content.append("Check-out Date: ").append(reservation.getCheckOutDate()).append("\n");
        }
        
        alert.setContentText(content.toString());
        
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
    
    private void showSuccessDialog(Stay stay, Reservation reservation, Room room) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Check-In Successful");
        alert.setHeaderText("Guest checked in successfully!");
        
        StringBuilder content = new StringBuilder();
        content.append("Stay Details:\n\n");
        content.append("Stay ID: ").append(stay.getId()).append("\n");
        content.append("Guest: ").append(reservation.getGuestName()).append("\n");
        content.append("Room: ").append(room.getRoomNumber()).append("\n");
        if (stay.getActualCheckin() != null) {
            content.append("Check-in Time: ").append(stay.getActualCheckin()).append("\n");
        }
        if (stay.getFolioId() != null) {
            content.append("Folio ID: ").append(stay.getFolioId()).append("\n");
        }
        content.append("\nThe guest can now be charged to the folio.");
        
        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

