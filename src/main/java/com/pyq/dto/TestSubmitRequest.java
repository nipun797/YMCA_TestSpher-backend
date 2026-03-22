package com.pyq.dto;

import lombok.Data;
import java.util.Map;

@Data
public class TestSubmitRequest {
    private Long subjectId;
    // JSON keys are always strings; service converts to Long
    private Map<String, String> answers;
}
