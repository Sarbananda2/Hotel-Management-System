package com.hotel.desktop.controller;

import com.hotel.dto.CreateRoomRequest;
import com.hotel.model.RoomType;
import com.hotel.service.RoomService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller("desktopCreateRoomController")
public class CreateRoomController implements Initializable {
    @Autowired
    private RoomService roomService;

    @FXML
    private TextField roomNumberField;

    @FXML
    private ComboBox<RoomType> roomTypeCombo;

    @FXML
    private Button createButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadRoomTypes();
        createButton.setOnAction(e -> handleCreateRoom());
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

    private void handleCreateRoom() {
        String roomNumber = roomNumberField.getText();
        RoomType roomType = roomTypeCombo.getValue();
        
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            showError("Error", "Missing Information", "Please enter a room number");
            return;
        }
        
        if (roomType == null) {
            showError("Error", "Missing Information", "Please select a room type");
            return;
        }

        try {
            CreateRoomRequest request = new CreateRoomRequest();
            request.setRoomNumber(roomNumber.trim());
            request.setRoomTypeId(roomType.getId());
            
            roomService.createRoom(request);
            showSuccess("Room Created", "Room created successfully!");
            clearForm();
        } catch (Exception e) {
            showError("Error", "Failed to create room", e.getMessage());
        }
    }

    private void clearForm() {
        roomNumberField.clear();
        roomTypeCombo.getSelectionModel().clearSelection();
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

