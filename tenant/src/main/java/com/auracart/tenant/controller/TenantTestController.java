package com.auracart.tenant.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tenant-test")
@RequiredArgsConstructor
public class TenantTestController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/ping")
    public String ping() {
        String currentDatabase = jdbcTemplate.queryForObject("SELECT current_database();", String.class);
        return "Successfully connected to database: " + currentDatabase;
    }

    @PostConstruct
    public void init() {
        System.out.println("TenantTestController loaded");
    }
}

