package com.examregistration.backend.controller;

import com.examregistration.backend.model.Registration;
import com.examregistration.backend.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // To get current user details
import org.springframework.security.core.userdetails.UserDetails; // Or your CustomUserDetails
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class as a REST controller
@RequestMapping("/api/registrations") // Base path for all endpoints in this controller
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /**
     * Allows a student to register for an exam.
     * The student's ID is taken from the authenticated user's token.
     * @param examId The ID of the exam to register for.
     * @param userDetails The authenticated user's details.
     * @return The created Registration object.
     */
    @PostMapping("/{examId}") // e.g., POST /api/registrations/123 (to register for exam ID 123)
    @PreAuthorize("hasRole('STUDENT')") // Only students can register
    public ResponseEntity<Registration> registerForExam(
            @PathVariable Long examId,
            @AuthenticationPrincipal UserDetails userDetails) { // Inject current user details
        try {
            // Assuming your UserDetails implementation (e.g., CustomUserDetails) has a method to get the actual user ID
            // If your UserDetails is CustomUserDetails, cast it: ((CustomUserDetails) userDetails).getUserId();
            // For now, let's assume username is the unique identifier you can use to fetch user from DB if needed
            // Or ideally, pass the User ID directly if your UserDetails object contains it.
            // For simplicity, let's assume your 'username' in UserDetails is directly the ID for now,
            // or you might need a service method to get User by username.
            // A more robust way: fetch User entity by username from UserRepository if your UserDetails doesn't contain ID.
            // For now, let's just make sure your User entity has a 'username' field for lookup.
            // Let's assume you pass the user ID from CustomUserDetails if you have it.
            // If not, you might need to resolve it from the DB using the username.

            // --- IMPORTANT: How to get the actual userId from UserDetails ---
            // If your UserDetails is a custom implementation (e.g., CustomUserDetails) and stores the ID:
            // Long userId = ((CustomUserDetails) userDetails).getId(); // Assuming CustomUserDetails has getId()

            // If you only have username in standard UserDetails and need to look up the ID:
            // You'd need to inject UserRepository and find user by username
            // User currentUser = userRepository.findByUsername(userDetails.getUsername())
            //                                  .orElseThrow(() -> new RuntimeException("Authenticated user not found in DB"));
            // Long userId = currentUser.getId();

            // For now, I'll use a placeholder. Replace this with how your application gets the current user's actual ID:
            Long currentAuthenticatedUserId = getUserIdFromUserDetails(userDetails); // Implement this helper method or use your CustomUserDetails.getId()


            Registration newRegistration = registrationService.registerForExam(currentAuthenticatedUserId, examId);
            return new ResponseEntity<>(newRegistration, HttpStatus.CREATED); // 201 Created
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // 400 Bad Request for validation errors
        } catch (Exception e) {
            // General error
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    /**
     * Allows a user to view all their own registrations.
     * @param userDetails The authenticated user's details.
     * @return List of registrations for the current user.
     */
    @GetMapping("/my") // e.g., GET /api/registrations/my
    @PreAuthorize("hasRole('STUDENT')") // Only students can view their own registrations
    public ResponseEntity<List<Registration>> getMyRegistrations(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long currentAuthenticatedUserId = getUserIdFromUserDetails(userDetails); // Implement this helper
            List<Registration> registrations = registrationService.getRegistrationsByUserId(currentAuthenticatedUserId);
            if (registrations.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
            }
            return new ResponseEntity<>(registrations, HttpStatus.OK); // 200 OK
        } catch (IllegalArgumentException e) {
            // This case should ideally not happen if user is authenticated
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Allows an Admin to view all registrations for a specific exam.
     * @param examId The ID of the exam.
     * @return List of registrations for that exam.
     */
    @GetMapping("/exam/{examId}") // e.g., GET /api/registrations/exam/123
    @PreAuthorize("hasRole('ADMIN')") // Only Admins can view registrations for any exam
    public ResponseEntity<List<Registration>> getRegistrationsByExam(@PathVariable Long examId) {
        try {
            List<Registration> registrations = registrationService.getRegistrationsByExamId(examId);
            if (registrations.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
            }
            return new ResponseEntity<>(registrations, HttpStatus.OK); // 200 OK
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // 404 Not Found if exam doesn't exist
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Allows a student to cancel their own registration, or an admin to cancel any registration.
     * @param registrationId The ID of the registration to cancel.
     * @param userDetails The authenticated user's details.
     * @return HTTP Status indicating success or failure.
     */
    @DeleteMapping("/{registrationId}") // e.g., DELETE /api/registrations/456
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')") // Both can cancel, but logic differs
    public ResponseEntity<HttpStatus> cancelRegistration(
            @PathVariable Long registrationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long currentAuthenticatedUserId = getUserIdFromUserDetails(userDetails); // Implement this helper

            // Check if current user is an ADMIN
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            Registration registration = registrationService.getRegistrationById(registrationId)
                    .orElseThrow(() -> new IllegalArgumentException("Registration not found."));

            // If not admin, ensure the user is canceling their own registration
            if (!isAdmin && !registration.getUser().getId().equals(currentAuthenticatedUserId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
            }

            registrationService.cancelRegistration(registrationId, currentAuthenticatedUserId); // Service handles core logic
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (IllegalArgumentException e) {
            // This could be for registration not found, or other service-level validation errors
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    // --- Helper method to extract user ID from UserDetails ---
    // You MUST implement this based on your CustomUserDetails or how you map username to ID.
    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        // --- Replace this with your actual logic ---
        // Option 1: If your CustomUserDetails extends UserDetails and stores the ID:
        if (userDetails instanceof com.examregistration.backend.config.CustomUserDetails) {
            return ((com.examregistration.backend.config.CustomUserDetails) userDetails).getUserId();
        }

        // Option 2: If you need to fetch the User from the database using username:
        // You'll need to inject UserRepository here (or pass it from constructor if you prefer).
        // @Autowired private UserRepository userRepository;
        // User user = userRepository.findByUsername(userDetails.getUsername())
        //                           .orElseThrow(() -> new RuntimeException("User not found: " + userDetails.getUsername()));
        // return user.getId();

        // Fallback/Error if neither is implemented:
        throw new UnsupportedOperationException("Cannot get user ID from UserDetails. Implement getUserIdFromUserDetails method.");
    }
}