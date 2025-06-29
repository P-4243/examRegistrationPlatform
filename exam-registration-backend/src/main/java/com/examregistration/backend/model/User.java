package com.examregistration.backend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set; // For roles, if using many-to-many

@Data // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields
@Entity // Marks this class as a JPA entity
@Table(name = "users") // Maps to the 'users' table in the database

public class User {

    @Id // Marks this as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increments ID
    private Long id;

    @Column(unique = true, nullable = false) // Ensures username is unique and not null
    private String username;

    @Column(nullable = false)
    private String password; // Will store hashed password

    @Column(unique = true, nullable = false) // Ensures email is unique and not null
    private String email;

    // We'll simplify roles for now, using a simple String.
    // For more complex role management, you'd use a separate Role entity and ManyToMany relationship.
    @Column(nullable = false)
    private String role; // e.g., "STUDENT", "ADMIN"

    // Optional: You might want to add OneToMany relationship to Registrations later
    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    // private Set<Registration> registrations;
}