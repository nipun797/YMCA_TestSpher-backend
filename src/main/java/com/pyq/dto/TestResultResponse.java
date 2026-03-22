package com.pyq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestResultResponse {
    private Long attemptId;
    private String studentName;
    private String subjectName;
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private double score;
    private String attemptDate;
}
