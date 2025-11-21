package com.hotel.desktop.controller;

import com.hotel.dto.DailyReportResponse;
import com.hotel.service.ReportService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

@Controller("desktopReportController")
public class ReportController implements Initializable {
    @Autowired
    private ReportService reportService;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Label totalRoomsLabel;

    @FXML
    private Label occupiedLabel;

    @FXML
    private Label occupancyPctLabel;

    @FXML
    private Label revenueLabel;

    @FXML
    private Label adrLabel;

    @FXML
    private Button generateButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.setValue(LocalDate.now());
        generateButton.setOnAction(e -> generateReport());
        generateReport(); // Load today's report by default
    }

    private void generateReport() {
        LocalDate date = datePicker.getValue();
        if (date == null) {
            showError("Error", "Missing Date", "Please select a date");
            return;
        }

        try {
            DailyReportResponse report = reportService.getDailyReport(date);
            totalRoomsLabel.setText(String.valueOf(report.getTotalRooms()));
            occupiedLabel.setText(String.valueOf(report.getOccupied()));
            occupancyPctLabel.setText(String.format("%.2f%%", report.getOccupancyPct()));
            revenueLabel.setText("₹" + report.getTotalCashRevenue());
            adrLabel.setText("₹" + report.getAdr());
        } catch (Exception e) {
            showError("Error", "Failed to generate report", e.getMessage());
        }
    }

    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

