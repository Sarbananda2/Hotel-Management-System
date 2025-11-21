package com.hotel.desktop.controller;

import com.hotel.dto.LoginResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller("desktopDashboardController")
public class DashboardController {
    @Autowired
    private ApplicationContext applicationContext;

    @FXML
    private BorderPane mainPane;

    @FXML
    private Label welcomeLabel;

    @FXML
    private MenuBar menuBar;

    private LoginResponse.UserDto currentUser;

    @FXML
    private void initialize() {
        try {
            currentUser = com.hotel.desktop.controller.LoginController.getCurrentUser();
            if (currentUser != null && welcomeLabel != null) {
                welcomeLabel.setText("Welcome, " + currentUser.getName() + " (" + currentUser.getRole() + ")");
                setupMenuBasedOnRole();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in DashboardController.initialize(): " + e.getMessage());
        }
    }

    private void setupMenuBasedOnRole() {
        if (menuBar == null) {
            return;
        }
        
        menuBar.getMenus().clear();
        
        // Reservations menu (all roles)
        Menu reservationsMenu = new Menu("Reservations");
        MenuItem viewReservations = new MenuItem("View Reservations");
        MenuItem createReservation = new MenuItem("Create Reservation");
        viewReservations.setOnAction(e -> loadView("reservations"));
        createReservation.setOnAction(e -> loadView("reservations")); // Same view has create form
        reservationsMenu.getItems().addAll(viewReservations, createReservation);
        menuBar.getMenus().add(reservationsMenu);

        // Stays menu (FRONTDESK, ADMIN)
        if ("FRONTDESK".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole())) {
            Menu staysMenu = new Menu("Stays");
            MenuItem checkIn = new MenuItem("Check In");
            MenuItem checkOut = new MenuItem("Check Out");
            checkIn.setOnAction(e -> loadView("checkin"));
            checkOut.setOnAction(e -> loadView("checkout"));
            staysMenu.getItems().addAll(checkIn, checkOut);
            menuBar.getMenus().add(staysMenu);
        }

        // Rooms menu
        Menu roomsMenu = new Menu("Rooms");
        MenuItem viewRooms = new MenuItem("View Rooms");
        viewRooms.setOnAction(e -> loadView("rooms"));
        roomsMenu.getItems().add(viewRooms);
        
        if ("ADMIN".equals(currentUser.getRole())) {
            MenuItem createRoom = new MenuItem("Create Room");
            createRoom.setOnAction(e -> loadView("create-room"));
            roomsMenu.getItems().add(createRoom);
        }
        menuBar.getMenus().add(roomsMenu);

        // Housekeeping menu
        if ("HOUSEKEEPING".equals(currentUser.getRole()) || "ADMIN".equals(currentUser.getRole())) {
            Menu housekeepingMenu = new Menu("Housekeeping");
            MenuItem viewTasks = new MenuItem("View Tasks");
            viewTasks.setOnAction(e -> loadView("housekeeping"));
            housekeepingMenu.getItems().add(viewTasks);
            menuBar.getMenus().add(housekeepingMenu);
        }

        // Reports menu
        Menu reportsMenu = new Menu("Reports");
        MenuItem dailyReport = new MenuItem("Daily Report");
        dailyReport.setOnAction(e -> loadView("reports"));
        reportsMenu.getItems().add(dailyReport);
        menuBar.getMenus().add(reportsMenu);

        // Admin menu
        if ("ADMIN".equals(currentUser.getRole())) {
            Menu adminMenu = new Menu("Admin");
            MenuItem auditLogs = new MenuItem("Audit Logs");
            auditLogs.setOnAction(e -> loadView("audit"));
            adminMenu.getItems().add(auditLogs);
            menuBar.getMenus().add(adminMenu);
        }

        // Logout
        Menu userMenu = new Menu("User");
        MenuItem logout = new MenuItem("Logout");
        logout.setOnAction(e -> handleLogout());
        userMenu.getItems().add(logout);
        menuBar.getMenus().add(userMenu);
    }

    private void loadView(String viewName) {
        if (mainPane == null) {
            System.err.println("Error: mainPane is null. Cannot load view: " + viewName);
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + viewName + ".fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent view = loader.load();
            mainPane.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load view");
            alert.setContentText("Could not load " + viewName + ": " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleLogout() {
        if (mainPane == null || mainPane.getScene() == null) {
            System.err.println("Error: Cannot logout - mainPane or scene is null");
            return;
        }
        
        try {
            // Clear current user
            com.hotel.desktop.controller.LoginController.setCurrentUser(null);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Hotel Management System - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

