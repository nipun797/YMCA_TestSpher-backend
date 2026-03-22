package com.pyq.repository;

import com.pyq.entity.Question;
import com.pyq.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findBySubject(Subject subject);
    List<Question> findBySubjectId(Long subjectId);
    long countBySubjectId(Long subjectId);
}
