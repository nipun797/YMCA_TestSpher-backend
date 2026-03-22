package com.pyq.controller;

import com.pyq.dto.TestResultResponse;
import com.pyq.entity.Question;
import com.pyq.entity.StudyMaterial;
import com.pyq.entity.Subject;
import com.pyq.service.QuestionService;
import com.pyq.service.StudyMaterialService;
import com.pyq.service.SubjectService;
import com.pyq.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TestService testService;

    @Autowired
    private StudyMaterialService studyMaterialService;

    @PostMapping("/subjects")
    public ResponseEntity<?> addSubject(@RequestBody Map<String, String> body) {
        try {
            Subject subject = subjectService.addSubject(body.get("subjectName"));
            return ResponseEntity.ok(subject);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    @PutMapping("/subjects/{id}")
    public ResponseEntity<?> updateSubject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String name = body.get("subjectName");
            if (name == null || name.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Subject name is required"));
            }
            Subject updated = subjectService.updateSubject(id, name.trim());
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable Long id) {
        try {
            subjectService.deleteSubject(id);
            return ResponseEntity.ok(Map.of("message", "Subject deleted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/questions")
    public ResponseEntity<?> addQuestion(@RequestBody Map<String, Object> body) {
        try {
            Long subjectId = Long.valueOf(body.get("subjectId").toString());
            Question question = new Question();
            question.setQuestionText(body.get("questionText").toString());
            question.setOptionA(body.get("optionA").toString());
            question.setOptionB(body.get("optionB").toString());
            question.setOptionC(body.get("optionC").toString());
            question.setOptionD(body.get("optionD").toString());
            question.setCorrectAnswer(body.get("correctAnswer").toString());
            question.setQuestionType(Question.Type.MCQ);
            Question saved = questionService.addQuestion(question, subjectId);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/questions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addQuestionMultipart(
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("questionText") String questionText,
            @RequestParam(value = "questionType", required = false, defaultValue = "MCQ") String questionType,
            @RequestParam(value = "optionA", required = false) String optionA,
            @RequestParam(value = "optionB", required = false) String optionB,
            @RequestParam(value = "optionC", required = false) String optionC,
            @RequestParam(value = "optionD", required = false) String optionD,
            @RequestParam(value = "correctAnswer", required = false) String correctAnswer,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment
    ) {
        try {
            Question.Type type = Question.Type.valueOf(questionType.trim().toUpperCase());

            Question question = new Question();
            question.setQuestionText(questionText);
            question.setQuestionType(type);

            if (type == Question.Type.MCQ) {
                if (optionA == null || optionB == null || optionC == null || optionD == null) {
                    throw new RuntimeException("All MCQ options (A, B, C, D) are required");
                }
                if (correctAnswer == null || correctAnswer.isBlank()) {
                    throw new RuntimeException("Correct answer is required");
                }
                String ca = correctAnswer.trim().toUpperCase();
                if (!List.of("A", "B", "C", "D").contains(ca)) {
                    throw new RuntimeException("Correct answer must be A, B, C, or D");
                }
                question.setOptionA(optionA.trim());
                question.setOptionB(optionB.trim());
                question.setOptionC(optionC.trim());
                question.setOptionD(optionD.trim());
                question.setCorrectAnswer(ca);
            } else if (type == Question.Type.TEXT) {
                if (correctAnswer == null || correctAnswer.isBlank()) {
                    throw new RuntimeException("Expected answer (for TEXT type) is required");
                }
                question.setCorrectAnswer(correctAnswer.trim());
            }

            if (attachment != null && !attachment.isEmpty()) {
                String mime = attachment.getContentType() != null ? attachment.getContentType() : "";
                boolean ok = mime.startsWith("image/") || mime.equalsIgnoreCase("application/pdf");
                if (!ok) {
                    throw new RuntimeException("Only image or PDF attachments are allowed");
                }

                Path uploadDir = Path.of("uploads").toAbsolutePath().normalize();
                Files.createDirectories(uploadDir);

                String original = attachment.getOriginalFilename() != null ? attachment.getOriginalFilename() : "file";
                String safeOriginal = original.replaceAll("[^a-zA-Z0-9._-]", "_");
                String filename = UUID.randomUUID() + "_" + safeOriginal;

                Path dest = uploadDir.resolve(filename).normalize();
                attachment.transferTo(dest);

                question.setAttachmentPath("/uploads/" + filename);
                question.setAttachmentMime(mime);
                question.setAttachmentName(original);
            }

            Question saved = questionService.addQuestion(question, subjectId);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        try {
            questionService.deleteQuestion(id);
            return ResponseEntity.ok(Map.of("message", "Question deleted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/results")
    public ResponseEntity<List<TestResultResponse>> getAllResults() {
        return ResponseEntity.ok(testService.getAllAttempts());
    }

    @PostMapping(value = "/materials", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadMaterial(
            @RequestParam("title") String title,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            if (title == null || title.isBlank()) throw new RuntimeException("Title is required");
            if (subjectId == null) throw new RuntimeException("Subject is required");
            if (file == null || file.isEmpty()) throw new RuntimeException("File is required");

            String mime = file.getContentType() != null ? file.getContentType() : "";
            boolean ok = mime.startsWith("image/") || mime.equalsIgnoreCase("application/pdf");
            if (!ok) throw new RuntimeException("Only image or PDF files are allowed");

            Path uploadDir = Path.of("uploads", "materials").toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);

            String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
            String safeOriginal = original.replaceAll("[^a-zA-Z0-9._-]", "_");
            String filename = UUID.randomUUID() + "_" + safeOriginal;

            Path dest = uploadDir.resolve(filename).normalize();
            file.transferTo(dest);

            StudyMaterial material = new StudyMaterial();
            material.setTitle(title.trim());
            material.setDescription(description != null ? description.trim() : null);
            material.setFilePath("/uploads/materials/" + filename);
            material.setFileMime(mime);
            material.setFileName(original);

            StudyMaterial saved = studyMaterialService.save(material, subjectId);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/materials")
    public ResponseEntity<List<StudyMaterial>> listMaterials(
            @RequestParam(value = "subjectId", required = false) Long subjectId
    ) {
        if (subjectId == null) return ResponseEntity.ok(studyMaterialService.listAll());
        return ResponseEntity.ok(studyMaterialService.listBySubject(subjectId));
    }

    @DeleteMapping("/materials/{id}")
    public ResponseEntity<?> deleteMaterial(@PathVariable Long id) {
        try {
            studyMaterialService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Material deleted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
