package com.pyq.repository;

import com.pyq.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsBySubjectName(String subjectName);
}
