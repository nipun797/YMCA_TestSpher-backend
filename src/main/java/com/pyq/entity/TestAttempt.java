package com.pyq.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "test_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private double score;

    private LocalDateTime attemptDate;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Answer> answers;

    @PrePersist
    public void prePersist() {
        this.attemptDate = LocalDateTime.now();
    }
}
