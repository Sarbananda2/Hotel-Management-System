package com.hotel.desktop.controller;

import com.hotel.model.Stay;
import com.hotel.service.StayService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@Controller("desktopCheckOutController")
public class CheckOutController implements Initializable {
    @Autowired
    private StayService stayService;

    @FXML
    private TableView<Stay> stayTable;

    @FXML
    private TableColumn<Stay, Integer> idColumn;

    @FXML
    private TableColumn<Stay, Integer> reservationIdColumn;

    @FXML
    private TableColumn<Stay, Integer> roomIdColumn;

    @FXML
    private TableColumn<Stay, String> checkInColumn;

    @FXML
    private Button checkOutButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadActiveStays();
        
        checkOutButton.setOnAction(e -> handleCheckOut());
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        checkInColumn.setCellValueFactory(cellData -> {
            OffsetDateTime checkIn = cellData.getValue().getActualCheckin();
            if (checkIn != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    checkIn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
    }

    private void loadActiveStays() {
        List<Stay> stays = stayService.findActiveStays();
        stayTable.setItems(FXCollections.observableArrayList(stays));
    }

    private void handleCheckOut() {
        Stay selected = stayTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Error", "No Selection", "Please select a stay to check out");
            return;
        }

        try {
            stayService.checkOut(selected.getId());
            showSuccess("Check-Out Successful", "Guest checked out successfully!");
            loadActiveStays();
        } catch (Exception e) {
            showError("Error", "Check-out Failed", e.getMessage());
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

