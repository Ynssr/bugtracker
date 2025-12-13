package com.bugtracker.pattern.factory;

import com.bugtracker.entity.Bug;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ExcelReport implements Report {

    @Override
    public String generate(List<Bug> bugs) {
        StringBuilder excel = new StringBuilder();

        // Header
        excel.append("Bug Raporu - Excel Formatında\n");
        excel.append("Toplam Bug: ").append(bugs.size()).append("\n\n");

        // Table Header
        excel.append(String.format("%-5s | %-30s | %-15s | %-12s | %-15s | %-20s | %-20s%n",
                "ID", "Başlık", "Durum", "Öncelik", "Önem", "Raporlayan", "Atanan"));
        excel.append("-".repeat(130)).append("\n");

        // Data Rows
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (Bug bug : bugs) {
            excel.append(String.format("%-5d | %-30s | %-15s | %-12s | %-15s | %-20s | %-20s%n",
                    bug.getId(),
                    truncate(bug.getTitle(), 30),
                    bug.getStatus().getDisplayName(),
                    bug.getPriority().getDisplayName(),
                    bug.getSeverity().getDisplayName(),
                    bug.getReporter() != null ? bug.getReporter().getUsername() : "N/A",
                    bug.getAssignee() != null ? bug.getAssignee().getUsername() : "Atanmamış"));
        }

        excel.append("\n");

        // Summary
        excel.append("Özet İstatislikler:\n");
        excel.append("- Toplam Bug:").append(bugs.size()).append("\n");
        excel.append("- Açık:").append(bugs.stream().filter(b -> b.getStatus().name().equals("OPEN")).count()).append("\n");
        excel.append("- Devam Eden:").append(bugs.stream().filter(b -> b.getStatus().name().equals("IN_PROGRESS")).count()).append("\n");
        excel.append("- Çözülmüş:").append(bugs.stream().filter(b -> b.getStatus().name().equals("RESOLVED")).count()).append("\n");
        excel.append("- Kapalı:").append(bugs.stream().filter(b -> b.getStatus().name().equals("CLOSED")).count()).append("\n");

        System.out.println("Excel Raporu oluşturuldu!");
        return excel.toString();
    }

    @Override
    public String getFormat() {
        return "Excel";
    }

    private String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
}