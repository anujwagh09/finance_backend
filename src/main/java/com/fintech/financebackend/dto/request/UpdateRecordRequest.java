package com.fintech.financebackend.dto.request;

import com.fintech.financebackend.enums.RecordType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateRecordRequest {

    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private RecordType type;

    private String category;

    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;

    private String notes;
}