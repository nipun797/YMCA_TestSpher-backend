package com.pyq.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    public enum Type {
        MCQ,
        TEXT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String questionText;

    private String optionA;

    private String optionB;

    private String optionC;

    private String optionD;

    // For MCQ: A/B/C/D. For TEXT: expected answer text.
    @Column(columnDefinition = "TEXT")
    private String correctAnswer;

    @Enumerated(EnumType.STRING)
    private Type questionType = Type.MCQ;

    // Served as a public URL under /uploads/... (optional)
    private String attachmentPath;
    private String attachmentMime;
    private String attachmentName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
}
