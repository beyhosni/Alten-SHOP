package com.alten.shop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "contacts")
@Data
@AllArgsConstructor
@Builder
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "Message is required")
    @Size(max = 300, message = "Message must not exceed 300 characters")
    @Column(nullable = false, length = 300)
    private String message;

    @Column(nullable = false, updatable = false)
    private Long createdAt;

    public Contact() {
        this.createdAt = System.currentTimeMillis();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
    }
}
