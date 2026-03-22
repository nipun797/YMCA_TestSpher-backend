package com.pyq.service;

import com.pyq.entity.Question;
import com.pyq.entity.Subject;
import com.pyq.repository.AnswerRepository;
import com.pyq.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private SubjectService subjectService;

    public Question addQuestion(Question question, Long subjectId) {
        Subject subject = subjectService.getSubjectById(subjectId);
        question.setSubject(subject);
        return questionRepository.save(question);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> getQuestionsBySubject(Long subjectId) {
        return questionRepository.findBySubjectId(subjectId);
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }

    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new RuntimeException("Question not found");
        }
        deleteQuestionCascade(id);
    }

    @Transactional
    protected void deleteQuestionCascade(Long id) {
        // Remove dependent rows first to satisfy MySQL foreign keys (answers.question_id -> questions.id)
        if (answerRepository.existsByQuestionId(id)) {
            answerRepository.deleteByQuestionId(id);
        }
        questionRepository.deleteById(id);
    }
}
