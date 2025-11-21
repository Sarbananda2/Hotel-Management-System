package com.hotel.desktop;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;

public class HotelManagementDesktopApp extends Application {
    public static ConfigurableApplicationContext springContext;
    private Parent rootNode;

    @Override
    public void init() throws Exception {
        // Wait for Spring context to be ready
        if (springContext == null) {
            throw new IllegalStateException("Spring context not initialized. Make sure DesktopLauncher.main() is used to start the application.");
        }
        
        // Load login screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        loader.setControllerFactory(springContext::getBean);
        rootNode = loader.load();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Hotel Management System");
        primaryStage.setScene(new Scene(rootNode, 800, 600));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        if (springContext != null) {
            springContext.close();
        }
        Platform.exit();
    }
}

