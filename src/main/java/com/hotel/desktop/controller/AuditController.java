package com.hotel.desktop.controller;

import com.hotel.model.AuditLog;
import com.hotel.service.AuditService;
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
import java.util.ResourceBundle;

@Controller("desktopAuditController")
public class AuditController implements Initializable {
    @Autowired
    private AuditService auditService;

    @FXML
    private TableView<AuditLog> auditTable;

    @FXML
    private TableColumn<AuditLog, Integer> idColumn;

    @FXML
    private TableColumn<AuditLog, Integer> userIdColumn;

    @FXML
    private TableColumn<AuditLog, String> actionColumn;

    @FXML
    private TableColumn<AuditLog, String> entityTypeColumn;

    @FXML
    private TableColumn<AuditLog, Integer> entityIdColumn;

    @FXML
    private TableColumn<AuditLog, String> timestampColumn;

    @FXML
    private TextField userIdFilterField;

    @FXML
    private TextField actionFilterField;

    @FXML
    private Button filterButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupTable();
            loadAuditLogs();
            
            filterButton.setOnAction(e -> filterLogs());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing AuditController: " + e.getMessage());
        }
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        entityTypeColumn.setCellValueFactory(new PropertyValueFactory<>("entityType"));
        entityIdColumn.setCellValueFactory(new PropertyValueFactory<>("entityId"));
        timestampColumn.setCellValueFactory(cellData -> {
            OffsetDateTime timestamp = cellData.getValue().getTimestamp();
            if (timestamp != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
    }

    private void loadAuditLogs() {
        try {
            Integer userId = parseUserId();
            String action = actionFilterField.getText().trim().isEmpty() ? null : actionFilterField.getText().trim();
            
            java.util.List<AuditLog> logs = auditService.getAuditLogs(userId, action, null, 100);
            auditTable.setItems(FXCollections.observableArrayList(logs));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading audit logs: " + e.getMessage());
            auditTable.setItems(FXCollections.observableArrayList());
        }
    }

    private Integer parseUserId() {
        String userIdText = userIdFilterField.getText().trim();
        if (userIdText.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(userIdText);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void filterLogs() {
        loadAuditLogs();
    }
}

