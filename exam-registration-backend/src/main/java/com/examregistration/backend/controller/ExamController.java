package com.examregistration.backend.controller;

import com.examregistration.backend.model.Exam;
import com.examregistration.backend.service.ExamService;
import jakarta.validation.Valid; // For @Valid annotation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Crucial for security
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class as a REST controller
@RequestMapping("/api/exams") // Base path for all endpoints in this controller
public class ExamController {

    private final ExamService examService;

    @Autowired // Injects ExamService
    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    // --- Endpoints for Exam Management ---

    // 1. Create a new Exam (Admin only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Only users with ADMIN role can create exams
    public ResponseEntity<Exam> createExam(@Valid @RequestBody Exam exam) {
        try {
            Exam createdExam = examService.createExam(exam);
            return new ResponseEntity<>(createdExam, HttpStatus.CREATED); // Returns 201 Created
        } catch (IllegalArgumentException e) {
            // Handle validation errors from service
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Returns 400 Bad Request
        }
    }

    // 2. Get an Exam by ID (Admin or Student)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')") // Admins and Students can view specific exams
    public ResponseEntity<Exam> getExamById(@PathVariable Long id) {
        return examService.getExamById(id)
                .map(exam -> new ResponseEntity<>(exam, HttpStatus.OK)) // Returns 200 OK
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // Returns 404 Not Found
    }

    // 3. Get all Exams (Admin only)
    @GetMapping("/all") // Using /all to differentiate from getActiveUpcomingExams
    @PreAuthorize("hasRole('ADMIN')") // Only ADMINs can view all exams (including inactive ones)
    public ResponseEntity<List<Exam>> getAllExams() {
        List<Exam> exams = examService.getAllExams();
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }

    // 4. Get all Active Upcoming Exams (Student or Admin - commonly used for students to browse)
    @GetMapping // No specific path, so it maps to /api/exams
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')") // Both can see upcoming exams
    public ResponseEntity<List<Exam>> getActiveUpcomingExams() {
        List<Exam> exams = examService.getActiveUpcomingExams();
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }

    // 5. Get Exams by Course Name (Student or Admin)
    @GetMapping("/course/{courseName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<List<Exam>> getExamsByCourseName(@PathVariable String courseName) {
        List<Exam> exams = examService.getExamsByCourseName(courseName);
        if (exams.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content if list is empty
        }
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }

    // 6. Update an existing Exam (Admin only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only ADMINs can update exams
    public ResponseEntity<Exam> updateExam(@PathVariable Long id, @Valid @RequestBody Exam updatedExam) {
        try {
            Exam exam = examService.updateExam(id, updatedExam);
            return new ResponseEntity<>(exam, HttpStatus.OK); // Returns 200 OK
        } catch (IllegalArgumentException e) {
            // This could be for not found or validation errors from service
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Returns 400 Bad Request
        }
    }

    // 7. Delete an Exam by ID (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only ADMINs can delete exams
    public ResponseEntity<HttpStatus> deleteExam(@PathVariable Long id) {
        try {
            examService.deleteExam(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Returns 204 No Content on successful deletion
        } catch (IllegalArgumentException e) {
            // Handle case where exam is not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Returns 404 Not Found
        }
    }
}