package com.examregistration.backend.service;

import com.examregistration.backend.model.Exam;
import com.examregistration.backend.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service // Marks this class as a Spring Service component
public class ExamService {

    private final ExamRepository examRepository;

    @Autowired // Injects ExamRepository into the service
    public ExamService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    // --- Business Logic Methods for Exam Management ---

    // 1. Create a new Exam
    public Exam createExam(Exam exam) {
        // You could add validation here before saving
        // For example, check if examDate is in the future
        if (exam.getExamDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Exam date cannot be in the past.");
        }
        return examRepository.save(exam);
    }

    // 2. Get an Exam by ID
    public Optional<Exam> getExamById(Long id) {
        return examRepository.findById(id);
    }

    // 3. Get all Exams
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    // 4. Update an existing Exam
    public Exam updateExam(Long id, Exam updatedExam) {
        Optional<Exam> existingExamOptional = examRepository.findById(id);
        if (existingExamOptional.isPresent()) {
            Exam existingExam = existingExamOptional.get();
            // Update fields that are allowed to be changed
            existingExam.setTitle(updatedExam.getTitle());
            existingExam.setDescription(updatedExam.getDescription());
            existingExam.setExamDate(updatedExam.getExamDate());
            existingExam.setDurationInMinutes(updatedExam.getDurationInMinutes());
            existingExam.setMaxMarks(updatedExam.getMaxMarks());
            existingExam.setPassingMarks(updatedExam.getPassingMarks());
            existingExam.setActive(updatedExam.isActive());
            existingExam.setCourseName(updatedExam.getCourseName()); // Update course name
            // createdBy should generally not be updated here, as it's who initially created it.

            // Add validation like for createExam
            if (existingExam.getExamDate().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Cannot update exam to a past date.");
            }

            return examRepository.save(existingExam);
        } else {
            // Or throw a custom exception like ExamNotFoundException
            throw new IllegalArgumentException("Exam with ID " + id + " not found.");
        }
    }

    // 5. Delete an Exam by ID
    public void deleteExam(Long id) {
        if (examRepository.existsById(id)) {
            examRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Exam with ID " + id + " not found for deletion.");
        }
    }

    // 6. Get all active upcoming exams (using the custom query method from repository)
    public List<Exam> getActiveUpcomingExams() {
        // This leverages the custom query method in ExamRepository
        return examRepository.findByActiveTrueOrderByExamDateAsc().stream()
                .filter(exam -> exam.getExamDate().isAfter(LocalDateTime.now()))
                .toList();
    }

    // 7. Get exams by course name
    public List<Exam> getExamsByCourseName(String courseName) {
        return examRepository.findByCourseName(courseName);
    }
}