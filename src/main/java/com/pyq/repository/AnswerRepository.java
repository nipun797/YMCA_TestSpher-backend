package com.pyq.repository;

import com.pyq.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByAttemptId(Long attemptId);
    boolean existsByQuestionId(Long questionId);

    @Transactional
    void deleteByQuestionId(Long questionId);
}
