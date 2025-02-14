package org.polymath.noteapi.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;

import java.util.Set;
import java.util.UUID;

@Entity
@Data
public class Notes {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime modifiedAt;
    private boolean archived;
    @ElementCollection
    @CollectionTable(name = "note_tags",joinColumns = @JoinColumn(name = "note_id"))
    private Set<String> tag = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private Users user;

}
