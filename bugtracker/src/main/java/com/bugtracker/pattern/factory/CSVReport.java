package com.bugtracker.pattern.factory;

import com.bugtracker.entity.Bug;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class CSVReport implements Report {

    @Override
    public String generate(List<Bug> bugs) {
        StringBuilder csv = new StringBuilder();

        // CSV Header
        csv.append("ID,Başlık,Açıklama,Durum,Öncelik,Önem Derecesi,Raporlayan,Atanan,Oluşturulma Tarihi\n");

        // Data Rows
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for (Bug bug : bugs) {
            csv.append(bug.getId()).append(",");
            csv.append("\"").append(escapeCsv(bug.getTitle())).append("\",");
            csv.append("\"").append(escapeCsv(bug.getDescription())).append("\",");
            csv.append(bug.getStatus().getDisplayName()).append(",");
            csv.append(bug.getPriority().getDisplayName()).append(",");
            csv.append(bug.getSeverity().getDisplayName()).append(",");
            csv.append(bug.getReporter() != null ? bug.getReporter().getUsername() : "").append(",");
            csv.append(bug.getAssignee() != null ? bug.getAssignee().getUsername() : "").append(",");
            csv.append(bug.getCreatedAt().format(formatter));
            csv.append("\n");
        }

        System.out.println("CSV Raporu oluşturuldu!");
        return csv.toString();
    }

    @Override
    public String getFormat() {
        return "Csv";
    }

    private String escapeCsv(String str) {
        if (str == null) return "";
        return str.replace("\"", "\"\""); // CSV escape
    }
}