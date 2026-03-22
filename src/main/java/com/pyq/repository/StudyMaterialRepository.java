package com.pyq.repository;

import com.pyq.entity.StudyMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {
    List<StudyMaterial> findAllByOrderByUploadedAtDesc();
    List<StudyMaterial> findBySubjectIdOrderByUploadedAtDesc(Long subjectId);
}

