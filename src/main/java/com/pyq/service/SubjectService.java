package com.pyq.service;

import com.pyq.entity.Subject;
import com.pyq.repository.QuestionRepository;
import com.pyq.repository.StudyMaterialRepository;
import com.pyq.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private StudyMaterialRepository studyMaterialRepository;

    public Subject addSubject(String subjectName) {
        if (subjectRepository.existsBySubjectName(subjectName)) {
            throw new RuntimeException("Subject already exists");
        }
        Subject subject = new Subject();
        subject.setSubjectName(subjectName);
        return subjectRepository.save(subject);
    }

    public Subject updateSubject(Long id, String subjectName) {
        Subject subject = getSubjectById(id);
        if (!subject.getSubjectName().equals(subjectName) && subjectRepository.existsBySubjectName(subjectName)) {
            throw new RuntimeException("Subject name already exists");
        }
        subject.setSubjectName(subjectName.trim());
        return subjectRepository.save(subject);
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
    }

    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new RuntimeException("Subject not found");
        }
        studyMaterialRepository.deleteAll(studyMaterialRepository.findBySubjectIdOrderByUploadedAtDesc(id));
        questionRepository.deleteAll(questionRepository.findBySubjectId(id));
        subjectRepository.deleteById(id);
    }
}
