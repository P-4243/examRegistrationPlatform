package com.examregistration.backend.repository;

import com.examregistration.backend.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Marks this interface as a Spring Data JPA repository component
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    // JpaRepository provides methods like save(), findById(), findAll(), deleteById(), etc.

    // Custom query methods:

    /**
     * Finds all registrations for a specific user (student).
     * @param userId The ID of the user.
     * @return A list of registrations for the specified user.
     */
    List<Registration> findByUser_Id(Long userId);

    /**
     * Finds all registrations for a specific exam.
     * @param examId The ID of the exam.
     * @return A list of registrations for the specified exam.
     */
    List<Registration> findByExam_Id(Long examId);

    /**
     * Finds a specific registration by user ID and exam ID.
     * Useful to check if a user is already registered for an exam.
     * @param userId The ID of the user.
     * @param examId The ID of the exam.
     * @return An Optional containing the registration if found, or empty if not.
     */
    Optional<Registration> findByUser_IdAndExam_Id(Long userId, Long examId);

    /**
     * Finds registrations by user ID and a specific status.
     * @param userId The ID of the user.
     * @param status The status of the registration (e.g., REGISTERED, CANCELLED).
     * @return A list of registrations matching the criteria.
     */
    List<Registration> findByUser_IdAndStatus(Long userId, Registration.RegistrationStatus status);

    /**
     * Finds registrations by exam ID and a specific status.
     * @param examId The ID of the exam.
     * @param status The status of the registration.
     * @return A list of registrations matching the criteria.
     */
    List<Registration> findByExam_IdAndStatus(Long examId, Registration.RegistrationStatus status);
}