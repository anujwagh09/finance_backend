package com.fintech.financebackend.dto.response;

import com.fintech.financebackend.enums.RecordType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CategorySummary {
    private String category;
    private RecordType type;
    private BigDecimal total;
}