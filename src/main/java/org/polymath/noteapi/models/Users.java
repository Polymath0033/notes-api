package org.polymath.noteapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true,nullable = false)
    private String email;
    @Column(nullable = false)
    @Size(min = 5)
    private String password;
    private String authToken;
    private LocalDateTime tokenExpiresAt;


}
