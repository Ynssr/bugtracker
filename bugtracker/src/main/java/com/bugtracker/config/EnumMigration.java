package com.bugtracker.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EnumMigration implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("ALTER TABLE bugs DROP CONSTRAINT IF EXISTS bugs_status_check");
            jdbcTemplate.execute("ALTER TABLE bugs DROP CONSTRAINT IF EXISTS bugs_priority_check");
            jdbcTemplate.execute("ALTER TABLE bugs DROP CONSTRAINT IF EXISTS bugs_severity_check");
            jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check");
            jdbcTemplate.update("UPDATE bugs SET status = 'OPEN' WHERE status = 'Open'");
            jdbcTemplate.update("UPDATE bugs SET status = 'IN_PROGRESS' WHERE status = 'In_progress'");
            jdbcTemplate.update("UPDATE bugs SET status = 'RESOLVED' WHERE status = 'Resolved'");
            jdbcTemplate.update("UPDATE bugs SET status = 'REOPENED' WHERE status = 'Reopened'");
            jdbcTemplate.update("UPDATE bugs SET status = 'CLOSED' WHERE status = 'Closed'");
            jdbcTemplate.update("UPDATE bugs SET priority = 'LOW' WHERE priority = 'Low'");
            jdbcTemplate.update("UPDATE bugs SET priority = 'MEDIUM' WHERE priority = 'Medium'");
            jdbcTemplate.update("UPDATE bugs SET priority = 'HIGH' WHERE priority = 'High'");
            jdbcTemplate.update("UPDATE bugs SET priority = 'CRITICAL' WHERE priority = 'Critical'");
            jdbcTemplate.update("UPDATE bugs SET severity = 'MINOR' WHERE severity = 'Minor'");
            jdbcTemplate.update("UPDATE bugs SET severity = 'MAJOR' WHERE severity = 'Major'");
            jdbcTemplate.update("UPDATE bugs SET severity = 'CRITICAL' WHERE severity = 'Critical'");
            jdbcTemplate.update("UPDATE bugs SET severity = 'BLOCKER' WHERE severity = 'Blocker'");
            jdbcTemplate.update("UPDATE users SET role = 'ADMIN' WHERE role = 'Admin'");
            jdbcTemplate.update("UPDATE users SET role = 'DEVELOPER' WHERE role = 'Developer'");
            jdbcTemplate.update("UPDATE users SET role = 'TESTER' WHERE role = 'Tester'");
            jdbcTemplate.update("UPDATE users SET role = 'REPORTER' WHERE role = 'Reporter'");
            jdbcTemplate.execute("ALTER TABLE bugs ADD CONSTRAINT bugs_status_check CHECK (status IN ('OPEN', 'IN_PROGRESS', 'RESOLVED', 'REOPENED', 'CLOSED'))");
            jdbcTemplate.execute("ALTER TABLE bugs ADD CONSTRAINT bugs_priority_check CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'))");
            jdbcTemplate.execute("ALTER TABLE bugs ADD CONSTRAINT bugs_severity_check CHECK (severity IN ('MINOR', 'MAJOR', 'CRITICAL', 'BLOCKER'))");
            jdbcTemplate.execute("ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('ADMIN', 'DEVELOPER', 'TESTER', 'REPORTER'))");
        } catch (Exception e) {
            log.warn("Enum migration hatasi: {}", e.getMessage());
        }
    }
}
