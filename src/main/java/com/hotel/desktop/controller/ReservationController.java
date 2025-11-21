package com.hotel.desktop.controller;

import com.hotel.dto.CreateReservationRequest;
import com.hotel.model.Reservation;
import com.hotel.model.RoomType;
import com.hotel.service.ReservationService;
import com.hotel.service.RoomService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

@Controller("desktopReservationController")
public class ReservationController implements Initializable {
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private RoomService roomService;

    @FXML
    private TableView<Reservation> reservationTable;

    @FXML
    private TableColumn<Reservation, Integer> idColumn;

    @FXML
    private TableColumn<Reservation, String> guestNameColumn;

    @FXML
    private TableColumn<Reservation, String> emailColumn;

    @FXML
    private TableColumn<Reservation, LocalDate> checkInColumn;

    @FXML
    private TableColumn<Reservation, LocalDate> checkOutColumn;

    @FXML
    private TableColumn<Reservation, String> statusColumn;

    @FXML
    private TextField guestNameField;

    @FXML
    private TextField guestEmailField;

    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<RoomType> roomTypeCombo;

    @FXML
    private DatePicker checkInDate;

    @FXML
    private DatePicker checkOutDate;

    @FXML
    private Button createButton;

    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupTable();
            loadRoomTypes();
            loadReservations();
            
            createButton.setOnAction(e -> handleCreateReservation());
            cancelButton.setOnAction(e -> handleCancelReservation());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing ReservationController: " + e.getMessage());
        }
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        guestNameColumn.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("guestEmail"));
        checkInColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        checkOutColumn.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadRoomTypes() {
        List<RoomType> roomTypes = roomService.findAllRoomTypes();
        roomTypeCombo.setItems(FXCollections.observableArrayList(roomTypes));
        
        // Set cell factory to display room type name
        roomTypeCombo.setCellFactory(param -> new javafx.scene.control.ListCell<RoomType>() {
            @Override
            protected void updateItem(RoomType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (₹" + item.getBaseRate() + "/night)");
                }
            }
        });
        
        // Set button cell to display selected item properly
        roomTypeCombo.setButtonCell(new javafx.scene.control.ListCell<RoomType>() {
            @Override
            protected void updateItem(RoomType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (₹" + item.getBaseRate() + "/night)");
                }
            }
        });
    }

    private void loadReservations() {
        try {
            List<Reservation> reservations = reservationService.findAll(null, null, null);
            reservationTable.setItems(FXCollections.observableArrayList(reservations));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading reservations: " + e.getMessage());
            reservationTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void handleCreateReservation() {
        try {
            CreateReservationRequest request = new CreateReservationRequest();
            request.setGuestName(guestNameField.getText());
            request.setGuestEmail(guestEmailField.getText());
            request.setPhone(phoneField.getText());
            request.setRoomTypeId(roomTypeCombo.getValue().getId());
            request.setCheckInDate(checkInDate.getValue());
            request.setCheckOutDate(checkOutDate.getValue());

            reservationService.create(request);
            showSuccess("Reservation Created", "Reservation created successfully!");
            clearForm();
            loadReservations();
        } catch (Exception e) {
            showError("Error", "Failed to create reservation", e.getMessage());
        }
    }

    private void handleCancelReservation() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Error", "No selection", "Please select a reservation to cancel");
            return;
        }

        try {
            reservationService.cancel(selected.getId());
            showSuccess("Reservation Cancelled", "Reservation cancelled successfully!");
            loadReservations();
        } catch (Exception e) {
            showError("Error", "Failed to cancel reservation", e.getMessage());
        }
    }

    private void clearForm() {
        guestNameField.clear();
        guestEmailField.clear();
        phoneField.clear();
        roomTypeCombo.getSelectionModel().clearSelection();
        checkInDate.setValue(null);
        checkOutDate.setValue(null);
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

