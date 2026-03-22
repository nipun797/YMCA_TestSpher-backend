package com.pyq.service;

import com.pyq.dto.TestResultResponse;
import com.pyq.dto.TestSubmitRequest;
import com.pyq.entity.*;
import com.pyq.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TestService {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    private TestAttemptRepository testAttemptRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectService subjectService;

    @Transactional
    public TestResultResponse submitTest(Long studentId, TestSubmitRequest request) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Subject subject = subjectService.getSubjectById(request.getSubjectId());

        // Convert String keys to Long
        Map<String, String> submittedAnswers = request.getAnswers();
        List<Long> questionIds = new ArrayList<>();
        for (String key : submittedAnswers.keySet()) {
            questionIds.add(Long.parseLong(key));
        }
        List<Question> questions = questionRepository.findAllById(questionIds);

        int correct = 0;
        int wrong = 0;
        List<Answer> answerList = new ArrayList<>();

        TestAttempt attempt = new TestAttempt();
        attempt.setStudent(student);
        attempt.setSubject(subject);
        attempt = testAttemptRepository.save(attempt);

        for (Question question : questions) {
            String selected = submittedAnswers.get(question.getId().toString());
            boolean isCorrect = selected != null &&
                    question.getCorrectAnswer().equalsIgnoreCase(selected);
            if (isCorrect) correct++;
            else wrong++;

            Answer answer = new Answer();
            answer.setAttempt(attempt);
            answer.setQuestion(question);
            answer.setSelectedOption(selected != null ? selected : "");
            answer.setCorrect(isCorrect);
            answerList.add(answer);
        }

        answerRepository.saveAll(answerList);

        double score = questions.isEmpty() ? 0 : ((double) correct / questions.size()) * 100;

        attempt.setTotalQuestions(questions.size());
        attempt.setCorrectAnswers(correct);
        attempt.setWrongAnswers(wrong);
        attempt.setScore(Math.round(score * 100.0) / 100.0);
        testAttemptRepository.save(attempt);

        return buildResponse(attempt, subject.getSubjectName());
    }

    public List<TestResultResponse> getStudentHistory(Long studentId) {
        List<TestAttempt> attempts = testAttemptRepository.findByStudentIdOrderByAttemptDateDesc(studentId);
        List<TestResultResponse> result = new ArrayList<>();
        for (TestAttempt a : attempts) {
            result.add(buildResponse(a, a.getSubject().getSubjectName()));
        }
        return result;
    }

    public List<TestResultResponse> getAllAttempts() {
        List<TestAttempt> attempts = testAttemptRepository.findAllByOrderByAttemptDateDesc();
        List<TestResultResponse> result = new ArrayList<>();
        for (TestAttempt a : attempts) {
            result.add(buildResponse(a, a.getSubject().getSubjectName()));
        }
        return result;
    }

    private TestResultResponse buildResponse(TestAttempt a, String subjectName) {
        String date = a.getAttemptDate() != null
                ? a.getAttemptDate().format(DATE_FMT)
                : "";
        return new TestResultResponse(
                a.getId(),
                a.getStudent() != null ? a.getStudent().getName() : "",
                subjectName,
                a.getTotalQuestions(),
                a.getCorrectAnswers(),
                a.getWrongAnswers(),
                a.getScore(),
                date
        );
    }
}
