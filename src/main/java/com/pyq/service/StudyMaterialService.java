package com.pyq.service;

import com.pyq.entity.StudyMaterial;
import com.pyq.entity.Subject;
import com.pyq.repository.StudyMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyMaterialService {

    @Autowired
    private StudyMaterialRepository repository;

    @Autowired
    private SubjectService subjectService;

    public StudyMaterial save(StudyMaterial material, Long subjectId) {
        if (subjectId == null) throw new RuntimeException("Subject is required");
        Subject subject = subjectService.getSubjectById(subjectId);
        material.setSubject(subject);
        return repository.save(material);
    }

    public List<StudyMaterial> listAll() {
        return repository.findAllByOrderByUploadedAtDesc();
    }

    public List<StudyMaterial> listBySubject(Long subjectId) {
        return repository.findBySubjectIdOrderByUploadedAtDesc(subjectId);
    }

    public StudyMaterial getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Material not found"));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Material not found");
        }
        repository.deleteById(id);
    }
}

