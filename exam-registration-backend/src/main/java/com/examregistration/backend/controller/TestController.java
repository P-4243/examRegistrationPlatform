package com.examregistration.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Make sure this is imported
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("This is a public endpoint. Anyone can access it.");
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')") // Accessible by authenticated STUDENT or ADMIN
    public ResponseEntity<String> userEndpoint() {
        return ResponseEntity.ok("This is a user-only endpoint. You are authenticated!");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')") // Only accessible by authenticated ADMIN
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("This is an admin-only endpoint. You are authenticated as an admin!");
    }
}