package com.fintech.financebackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private Map<String, String> fields;  
    private LocalDateTime timestamp;
}