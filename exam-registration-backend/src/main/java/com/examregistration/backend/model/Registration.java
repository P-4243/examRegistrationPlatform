package com.examregistration.backend.model;

import jakarta.persistence.*; // Use jakarta.persistence for Spring Boot 3+
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // Lombok annotation for getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok annotation for no-argument constructor
@AllArgsConstructor // Lombok annotation for all-argument constructor
@Entity // Marks this class as a JPA entity
@Table(name = "registrations", uniqueConstraints = {
        // Ensures a user can register for a specific exam only once
        @UniqueConstraint(columnNames = {"user_id", "exam_id"})
})
public class Registration {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates the ID
    private Long id;

    // --- Relationships ---

    @ManyToOne(fetch = FetchType.LAZY) // Many registrations to one user
    @JoinColumn(name = "user_id", nullable = false) // Foreign key column for User
    private User user; // The user (student) who registered

    @ManyToOne(fetch = FetchType.LAZY) // Many registrations to one exam
    @JoinColumn(name = "exam_id", nullable = false) // Foreign key column for Exam
    private Exam exam; // The exam being registered for

    // --- Registration Details ---

    @Column(nullable = false)
    private LocalDateTime registrationDate; // When the registration occurred

    @Enumerated(EnumType.STRING) // Store enum as String in DB
    @Column(nullable = false)
    private RegistrationStatus status; // Current status of the registration

    // Optional: If you want to store a score/grade here later
    @Column
    private Integer score; // Score obtained in the exam

    // Enum for Registration Status
    public enum RegistrationStatus {
        REGISTERED,
        CANCELLED,
        COMPLETED, // After the exam is taken and possibly graded
        MISSED // If student didn't attend
    }

    // You can add more fields as needed, e.g., payment status, attendance, etc.
}