package com.pyq.controller;

import com.pyq.config.JwtUtil;
import com.pyq.dto.TestResultResponse;
import com.pyq.dto.TestSubmitRequest;
import com.pyq.entity.Question;
import com.pyq.entity.StudyMaterial;
import com.pyq.entity.Subject;
import com.pyq.entity.User;
import com.pyq.repository.UserRepository;
import com.pyq.service.QuestionService;
import com.pyq.service.StudyMaterialService;
import com.pyq.service.SubjectService;
import com.pyq.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TestService testService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StudyMaterialService studyMaterialService;

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    @GetMapping("/questions/{subjectId}")
    public ResponseEntity<List<Question>> getQuestions(@PathVariable Long subjectId) {
        return ResponseEntity.ok(questionService.getQuestionsBySubject(subjectId));
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitTest(@RequestHeader("Authorization") String authHeader,
                                        @RequestBody TestSubmitRequest request) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            TestResultResponse result = testService.submitTest(user.getId(), request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<TestResultResponse> history = testService.getStudentHistory(user.getId());
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/materials")
    public ResponseEntity<List<StudyMaterial>> getMaterials(
            @RequestParam(value = "subjectId", required = false) Long subjectId
    ) {
        if (subjectId == null) return ResponseEntity.ok(studyMaterialService.listAll());
        return ResponseEntity.ok(studyMaterialService.listBySubject(subjectId));
    }

    @GetMapping("/materials/{id}/download")
    public ResponseEntity<?> downloadMaterial(@PathVariable Long id) {
        try {
            StudyMaterial m = studyMaterialService.getById(id);
            // filePath is like "/uploads/materials/<file>"
            String rel = m.getFilePath().startsWith("/") ? m.getFilePath().substring(1) : m.getFilePath();
            Path filePath = Path.of(rel).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File not found on server"));
            }
            String filename = m.getFileName() != null ? m.getFileName() : "material";
            String mime = m.getFileMime() != null && !m.getFileMime().isBlank() ? m.getFileMime() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mime))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename.replace("\"", "") + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
