package com.bugtracker.pattern.factory;

import com.bugtracker.entity.Bug;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PDFReport implements Report {

    @Override
    public String generate(List<Bug> bugs) {
        StringBuilder pdf = new StringBuilder();

        pdf.append("=".repeat(80)).append("\n");
        pdf.append("Bug Raporu (Pdf Formatında)\n");
        pdf.append("=".repeat(80)).append("\n");
        pdf.append("Oluşturulma Tarihi:").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))).append("\n");
        pdf.append("Toplam Bug Sayısı:").append(bugs.size()).append("\n");
        pdf.append("=".repeat(80)).append("\n\n");

        int counter = 1;
        for (Bug bug : bugs) {
            pdf.append("Bug #").append(counter++).append("\n");
            pdf.append("-".repeat(80)).append("\n");
            pdf.append("ID:").append(bug.getId()).append("\n");
            pdf.append("Başlık:").append(bug.getTitle()).append("\n");
            pdf.append("Durum:").append(bug.getStatus().getDisplayName()).append("\n");
            pdf.append("Öncelik:").append(bug.getPriority().getDisplayName()).append("\n");
            pdf.append("Önem Derecesi:").append(bug.getSeverity().getDisplayName()).append("\n");

            if (bug.getReporter() != null) {
                pdf.append("Raporlayan:").append(bug.getReporter().getUsername()).append("\n");
            }

            if (bug.getAssignee() != null) {
                pdf.append("Atanan:").append(bug.getAssignee().getUsername()).append("\n");
            }

            if (bug.getDescription() != null) {
                pdf.append("Açıklama:").append(bug.getDescription()).append("\n");
            }

            pdf.append("Oluşturma:").append(bug.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))).append("\n");
            pdf.append("\n");
        }

        pdf.append("=".repeat(80)).append("\n");
        pdf.append("Rapor Sonu\n");
        pdf.append("=".repeat(80)).append("\n");

        System.out.println("PDF Raporu oluşturuldu!");
        return pdf.toString();
    }

    @Override
    public String getFormat() {
        return "Pdf";
    }
}