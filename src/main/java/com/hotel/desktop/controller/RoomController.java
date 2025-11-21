package com.hotel.desktop.controller;

import com.hotel.model.Room;
import com.hotel.service.RoomService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller("desktopRoomController")
public class RoomController implements Initializable {
    @Autowired
    private RoomService roomService;

    @FXML
    private TableView<Room> roomTable;

    @FXML
    private TableColumn<Room, Integer> idColumn;

    @FXML
    private TableColumn<Room, String> roomNumberColumn;

    @FXML
    private TableColumn<Room, Integer> roomTypeIdColumn;

    @FXML
    private TableColumn<Room, String> statusColumn;

    @FXML
    private ComboBox<String> statusCombo;

    @FXML
    private Button updateStatusButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadRooms();
        setupStatusCombo();
        
        updateStatusButton.setOnAction(e -> handleUpdateStatus());
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeIdColumn.setCellValueFactory(new PropertyValueFactory<>("roomTypeId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadRooms() {
        List<Room> rooms = roomService.findAll();
        roomTable.setItems(FXCollections.observableArrayList(rooms));
    }

    private void setupStatusCombo() {
        statusCombo.setItems(FXCollections.observableArrayList("VACANT", "OCCUPIED", "CLEAN", "DIRTY", "MAINTENANCE"));
    }

    private void handleUpdateStatus() {
        Room selected = roomTable.getSelectionModel().getSelectedItem();
        String newStatus = statusCombo.getValue();
        
        if (selected == null || newStatus == null) {
            showError("Error", "Missing Information", "Please select a room and status");
            return;
        }

        try {
            com.hotel.dto.UpdateRoomStatusRequest request = new com.hotel.dto.UpdateRoomStatusRequest();
            request.setStatus(newStatus);
            roomService.updateRoomStatus(selected.getId(), request);
            showSuccess("Status Updated", "Room status updated successfully!");
            loadRooms();
        } catch (Exception e) {
            showError("Error", "Update Failed", e.getMessage());
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

