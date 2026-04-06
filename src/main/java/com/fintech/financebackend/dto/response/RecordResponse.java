// RecordResponse.java
package com.fintech.financebackend.dto.response;

import com.fintech.financebackend.enums.RecordType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RecordResponse {
    private Long id;
    private BigDecimal amount;
    private RecordType type;
    private String category;
    private LocalDate date;
    private String notes;
    private String createdBy;       
    private LocalDateTime createdAt;
}