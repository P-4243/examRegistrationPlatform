package com.examregistration.backend.service;

import com.examregistration.backend.model.Exam;
import com.examregistration.backend.model.Registration;
import com.examregistration.backend.model.User; // Assuming your User entity is here
import com.examregistration.backend.repository.ExamRepository;
import com.examregistration.backend.repository.RegistrationRepository;
import com.examregistration.backend.repository.UserRepository; // Assuming your UserRepository is here
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // For transactional operations

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.examregistration.backend.model.Registration.RegistrationStatus;

@Service // Marks this class as a Spring Service component
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository; // To find the user registering
    private final ExamRepository examRepository; // To find the exam being registered for

    @Autowired
    public RegistrationService(RegistrationRepository registrationRepository,
                               UserRepository userRepository,
                               ExamRepository examRepository) {
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.examRepository = examRepository;
    }

    // --- Business Logic Methods for Registration Management ---

    /**
     * Handles a user registering for an exam.
     * @param userId The ID of the user attempting to register.
     * @param examId The ID of the exam.
     * @return The created Registration object.
     * @throws IllegalArgumentException if validation fails (e.g., exam not found, user not found, already registered).
     */
    @Transactional // Ensures the entire method runs as a single database transaction
    public Registration registerForExam(Long userId, Long examId) {
        // 1. Validate User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found."));

        // 2. Validate Exam
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam with ID " + examId + " not found."));

        // 3. Perform Business Rule Validations
        if (!exam.isActive()) {
            throw new IllegalArgumentException("Exam is not active for registration.");
        }
        if (exam.getRegistrationDeadline() != null && LocalDateTime.now().isAfter(exam.getRegistrationDeadline())) {
            throw new IllegalArgumentException("Registration deadline for this exam has passed.");
        }
        if (exam.getExamDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot register for an exam that has already occurred.");
        }

        // 4. Check for duplicate registration using the unique constraint logic
        Optional<Registration> existingRegistration = registrationRepository.findByUser_IdAndExam_Id(userId, examId);
        if (existingRegistration.isPresent()) {
            // Check if existing registration is CANCELLED and can be re-registered
            if (existingRegistration.get().getStatus() == RegistrationStatus.CANCELLED) {
                // Option: Re-activate or create a new one. For simplicity, let's create a new one or update status
                Registration registration = existingRegistration.get();
                registration.setStatus(RegistrationStatus.REGISTERED);
                registration.setRegistrationDate(LocalDateTime.now());
                return registrationRepository.save(registration);
            } else {
                throw new IllegalArgumentException("User is already registered for this exam.");
            }
        }

        // 5. Create new Registration
        Registration newRegistration = new Registration();
        newRegistration.setUser(user);
        newRegistration.setExam(exam);
        newRegistration.setRegistrationDate(LocalDateTime.now());
        newRegistration.setStatus(RegistrationStatus.REGISTERED); // Set initial status

        return registrationRepository.save(newRegistration);
    }

    /**
     * Allows a user to cancel their registration for an exam.
     * @param registrationId The ID of the registration to cancel.
     * @param userId The ID of the user attempting to cancel (for authorization check).
     * @return The updated Registration object.
     * @throws IllegalArgumentException if registration not found or user is not authorized.
     */
    @Transactional
    public Registration cancelRegistration(Long registrationId, Long userId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Registration with ID " + registrationId + " not found."));

        // Ensure the user trying to cancel is the one who registered, or an ADMIN
        if (!registration.getUser().getId().equals(userId) /* && !currentUserIsAdmin() */) { // You'll need to check role here if you want admins to cancel for others
            // For now, assume only the registering user can cancel. Admin check will be in controller.
            throw new IllegalArgumentException("User is not authorized to cancel this registration.");
        }

        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new IllegalArgumentException("Registration is already cancelled.");
        }
        // Optional: Prevent cancellation if exam is too close or already passed
        if (registration.getExam().getExamDate().isBefore(LocalDateTime.now().plusHours(24))) { // e.g., cannot cancel within 24 hours
            throw new IllegalArgumentException("Cannot cancel registration less than 24 hours before the exam.");
        }

        registration.setStatus(RegistrationStatus.CANCELLED);
        return registrationRepository.save(registration);
    }

    /**
     * Get all registrations for a specific user.
     * @param userId The ID of the user.
     * @return List of registrations.
     */
    public List<Registration> getRegistrationsByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found."));
        return registrationRepository.findByUser_Id(userId);
    }

    /**
     * Get all registrations for a specific exam. (Typically for Admin view)
     * @param examId The ID of the exam.
     * @return List of registrations.
     */
    public List<Registration> getRegistrationsByExamId(Long examId) {
        examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam with ID " + examId + " not found."));
        return registrationRepository.findByExam_Id(examId);
    }

    /**
     * Get a single registration by its ID.
     * @param registrationId The ID of the registration.
     * @return Optional containing the registration.
     */
    public Optional<Registration> getRegistrationById(Long registrationId) {
        return registrationRepository.findById(registrationId);
    }
}