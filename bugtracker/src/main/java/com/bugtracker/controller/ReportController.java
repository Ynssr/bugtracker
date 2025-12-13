package com.bugtracker.controller;

import com.bugtracker.enums.BugStatus;
import com.bugtracker.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/formats")
    public ResponseEntity<List<String>> getAvailableFormats() {
        return ResponseEntity.ok(reportService.getAvailableFormats());
    }

    @GetMapping("/generate")
    public ResponseEntity<String> generateReport(@RequestParam String format) {
        String report = reportService.generateReport(format);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/generate/status/{status}")
    public ResponseEntity<String> generateReportByStatus(
            @PathVariable BugStatus status,
            @RequestParam String format) {

        String report = reportService.generateReportByStatus(format, status);
        return ResponseEntity.ok(report);
    }
}