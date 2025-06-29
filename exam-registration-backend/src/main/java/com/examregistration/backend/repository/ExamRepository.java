package com.examregistration.backend.repository;

import com.examregistration.backend.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Marks this interface as a Spring Data JPA repository component
public interface ExamRepository extends JpaRepository<Exam, Long> {
    // JpaRepository provides methods like save(), findById(), findAll(), deleteById(), etc.

    // You can add custom query methods here if needed, for example:
    List<Exam> findByActiveTrueOrderByExamDateAsc(); // Find all active exams, ordered by date
    List<Exam> findByCourseName(String courseName); // Find exams by course name

    // You can define more complex queries if necessary
    // @Query("SELECT e FROM Exam e WHERE e.examDate > CURRENT_TIMESTAMP AND e.active = true ORDER BY e.examDate ASC")
    // List<Exam> findUpcomingActiveExams();
}