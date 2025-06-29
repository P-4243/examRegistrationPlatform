package com.examregistration.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exams")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime examDate;

    @Column(nullable = false)
    private Integer durationInMinutes;

    @Column(nullable = false)
    private Integer maxMarks;

    @Column(nullable = false)
    private Integer passingMarks;

    @Column(nullable = false)
    private boolean active = true;

    @Column
    private String courseName;

    @Column
    private String createdBy;

    @Column(nullable = false)
    private Integer maxCapacity;

    @Column(nullable = false) // Assuming it's required based on the error
    private String name; // Or whatever type/nullable you decided for 'name'

    // NEW FIELD: registrationDeadline
    @Column(nullable = false) // Assuming it's a required field
    private LocalDateTime registrationDeadline;
}