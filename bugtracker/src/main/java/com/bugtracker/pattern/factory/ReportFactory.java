package com.bugtracker.pattern.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReportFactory {

    private final List<Report> availableReports;

    public Report createReport(String format) {
        return availableReports.stream()
                .filter(report -> report.getFormat().equalsIgnoreCase(format))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Desteklenmeyen rapor formatı: " + format +
                                ". Kullanılabilir formatlar: Pdf, Excel, Csv"));
    }

    public List<String> getAvailableFormats() {
        return availableReports.stream()
                .map(Report::getFormat)
                .toList();
    }
}