package com.pyq.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "study_materials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = true)
    private Subject subject;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Public URL under /uploads/materials/...
    @NotBlank
    private String filePath;

    private String fileMime;
    private String fileName;

    private LocalDateTime uploadedAt;

    @PrePersist
    public void prePersist() {
        this.uploadedAt = LocalDateTime.now();
    }
}

