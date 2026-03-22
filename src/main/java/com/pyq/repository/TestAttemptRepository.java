package com.pyq.repository;

import com.pyq.entity.TestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {
    List<TestAttempt> findByStudentId(Long studentId);
    List<TestAttempt> findByStudentIdOrderByAttemptDateDesc(Long studentId);
    List<TestAttempt> findAllByOrderByAttemptDateDesc();
}
