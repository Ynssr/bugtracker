package com.bugtracker.service;

import com.bugtracker.entity.Bug;
import com.bugtracker.enums.BugStatus;
import com.bugtracker.pattern.factory.Report;
import com.bugtracker.pattern.factory.ReportFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportFactory reportFactory;
    private final BugService bugService;

    public String generateReport(String format) {
        Report report = reportFactory.createReport(format);
        List<Bug> allBugs = bugService.getAllBugs();
        return report.generate(allBugs);
    }

    public String generateReportByStatus(String format, BugStatus status) {
        Report report = reportFactory.createReport(format);
        List<Bug> bugs = bugService.getBugsByStatus(status);
        return report.generate(bugs);
    }

    public List<String> getAvailableFormats() {
        return reportFactory.getAvailableFormats();
    }
}