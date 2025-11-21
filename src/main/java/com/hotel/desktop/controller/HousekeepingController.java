package com.hotel.desktop.controller;

import com.hotel.dto.CreateHousekeepingTaskRequest;
import com.hotel.dto.UpdateHousekeepingTaskRequest;
import com.hotel.model.HousekeepingTask;
import com.hotel.model.Room;
import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import com.hotel.service.HousekeepingService;
import com.hotel.service.RoomService;
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

@Controller("desktopHousekeepingController")
public class HousekeepingController implements Initializable {
    @Autowired
    private HousekeepingService housekeepingService;
    
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private UserRepository userRepository;

    @FXML
    private TableView<HousekeepingTask> taskTable;

    @FXML
    private TableColumn<HousekeepingTask, Integer> idColumn;

    @FXML
    private TableColumn<HousekeepingTask, Integer> roomIdColumn;

    @FXML
    private TableColumn<HousekeepingTask, String> statusColumn;

    @FXML
    private TableColumn<HousekeepingTask, Integer> assignedToColumn;

    @FXML
    private TableColumn<HousekeepingTask, String> notesColumn;

    @FXML
    private TableColumn<HousekeepingTask, String> createdAtColumn;

    @FXML
    private ComboBox<String> statusFilterCombo;

    @FXML
    private ComboBox<Room> roomCombo;

    @FXML
    private TextArea notesField;

    @FXML
    private Button createTaskButton;

    @FXML
    private ComboBox<String> updateStatusCombo;

    @FXML
    private ComboBox<User> assignToCombo;

    @FXML
    private TextArea updateNotesField;

    @FXML
    private Button updateTaskButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupFilters();
        loadTasks(null);
        loadRooms();
        loadUsers();
        
        statusFilterCombo.setOnAction(e -> filterTasks());
        createTaskButton.setOnAction(e -> handleCreateTask());
        updateTaskButton.setOnAction(e -> handleUpdateTask());
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        roomIdColumn.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        assignedToColumn.setCellValueFactory(new PropertyValueFactory<>("assignedTo"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        createdAtColumn.setCellValueFactory(cellData -> {
            OffsetDateTime createdAt = cellData.getValue().getCreatedAt();
            if (createdAt != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
    }

    private void setupFilters() {
        statusFilterCombo.setItems(FXCollections.observableArrayList("All", "OPEN", "IN_PROGRESS", "COMPLETED"));
        statusFilterCombo.setValue("All");
        updateStatusCombo.setItems(FXCollections.observableArrayList("OPEN", "IN_PROGRESS", "COMPLETED"));
    }

    private void loadTasks(String status) {
        List<HousekeepingTask> tasks = housekeepingService.findAll(status);
        taskTable.setItems(FXCollections.observableArrayList(tasks));
    }

    private void loadRooms() {
        List<Room> rooms = roomService.findAll();
        roomCombo.setItems(FXCollections.observableArrayList(rooms));
    }

    private void loadUsers() {
        List<User> users = userRepository.findAll();
        assignToCombo.setItems(FXCollections.observableArrayList(users));
    }

    private void filterTasks() {
        String status = statusFilterCombo.getValue();
        if ("All".equals(status)) {
            loadTasks(null);
        } else {
            loadTasks(status);
        }
    }

    private void handleCreateTask() {
        Room room = roomCombo.getValue();
        if (room == null) {
            showError("Error", "Missing Information", "Please select a room");
            return;
        }

        try {
            CreateHousekeepingTaskRequest request = new CreateHousekeepingTaskRequest();
            request.setRoomId(room.getId());
            request.setNotes(notesField.getText());
            
            housekeepingService.create(request);
            showSuccess("Task Created", "Housekeeping task created successfully!");
            notesField.clear();
            roomCombo.getSelectionModel().clearSelection();
            loadTasks(statusFilterCombo.getValue().equals("All") ? null : statusFilterCombo.getValue());
        } catch (Exception e) {
            showError("Error", "Failed to create task", e.getMessage());
        }
    }

    private void handleUpdateTask() {
        HousekeepingTask selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Error", "No Selection", "Please select a task to update");
            return;
        }

        try {
            UpdateHousekeepingTaskRequest request = new UpdateHousekeepingTaskRequest();
            if (updateStatusCombo.getValue() != null) {
                request.setStatus(updateStatusCombo.getValue());
            }
            if (assignToCombo.getValue() != null) {
                request.setAssignedTo(assignToCombo.getValue().getId());
            }
            if (updateNotesField.getText() != null && !updateNotesField.getText().trim().isEmpty()) {
                request.setNotes(updateNotesField.getText());
            }
            
            housekeepingService.update(selected.getId(), request);
            showSuccess("Task Updated", "Housekeeping task updated successfully!");
            updateStatusCombo.getSelectionModel().clearSelection();
            assignToCombo.getSelectionModel().clearSelection();
            updateNotesField.clear();
            loadTasks(statusFilterCombo.getValue().equals("All") ? null : statusFilterCombo.getValue());
        } catch (Exception e) {
            showError("Error", "Failed to update task", e.getMessage());
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

