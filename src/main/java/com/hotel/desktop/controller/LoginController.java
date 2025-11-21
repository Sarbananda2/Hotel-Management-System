package com.hotel.desktop.controller;

import com.hotel.dto.LoginRequest;
import com.hotel.dto.LoginResponse;
import com.hotel.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

@Controller("desktopLoginController")
public class LoginController {
    @Autowired
    private AuthService authService;
    
    @Autowired
    private ApplicationContext applicationContext;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private static LoginResponse.UserDto currentUser;

    @FXML
    private void handleLogin() {
        try {
            LoginRequest request = new LoginRequest();
            request.setEmail(emailField.getText());
            request.setPassword(passwordField.getText());

            LoginResponse response = authService.login(request);
            currentUser = response.getUser();

            // Load dashboard
            loadDashboard();
        } catch (Exception e) {
            showError("Login Failed", "Invalid email or password", e.getMessage());
        }
    }

    private void loadDashboard() {
        try {
            // Verify controller can be retrieved
            try {
                Object controller = applicationContext.getBean("desktopDashboardController");
                System.out.println("Controller retrieved: " + controller.getClass().getName());
            } catch (Exception e) {
                System.err.println("Failed to get controller bean: " + e.getMessage());
                e.printStackTrace();
            }
            
            java.net.URL fxmlUrl = getClass().getResource("/fxml/dashboard.fxml");
            if (fxmlUrl == null) {
                showError("Error", "FXML Not Found", "Cannot find dashboard.fxml in resources");
                return;
            }
            
            System.out.println("Loading FXML from: " + fxmlUrl);
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            System.out.println("FXML loaded successfully");
            
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
            stage.setTitle("Hotel Management System - Dashboard");
        } catch (javafx.fxml.LoadException e) {
            e.printStackTrace();
            String errorMsg = "LoadException: " + (e.getMessage() != null ? e.getMessage() : "null");
            Throwable cause = e.getCause();
            int depth = 0;
            while (cause != null && depth < 5) {
                errorMsg += "\nCaused by: " + cause.getClass().getSimpleName() + ": " + (cause.getMessage() != null ? cause.getMessage() : "null");
                cause = cause.getCause();
                depth++;
            }
            showError("Error", "Failed to load dashboard", errorMsg);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getClass().getSimpleName() + ": " + (e.getMessage() != null ? e.getMessage() : "null");
            Throwable cause = e.getCause();
            int depth = 0;
            while (cause != null && depth < 5) {
                errorMsg += "\nCaused by: " + cause.getClass().getSimpleName() + ": " + (cause.getMessage() != null ? cause.getMessage() : "null");
                cause = cause.getCause();
                depth++;
            }
            showError("Error", "Failed to load dashboard", errorMsg);
        }
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static LoginResponse.UserDto getCurrentUser() {
        return currentUser;
    }
    
    public static void setCurrentUser(LoginResponse.UserDto user) {
        currentUser = user;
    }
}

