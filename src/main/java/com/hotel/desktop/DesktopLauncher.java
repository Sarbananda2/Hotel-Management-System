package com.hotel.desktop;

import com.hotel.HotelManagementApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class DesktopLauncher {
    public static void main(String[] args) {
        // Start Spring Boot context first
        ConfigurableApplicationContext context = SpringApplication.run(HotelManagementApplication.class, args);
        HotelManagementDesktopApp.springContext = context;
        
        // Launch JavaFX on JavaFX Application Thread
        javafx.application.Application.launch(HotelManagementDesktopApp.class, args);
    }
}

