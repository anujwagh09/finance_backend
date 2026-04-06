package com.fintech.financebackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MonthlyTrend {
    private String month;           
    private BigDecimal income;
    private BigDecimal expense;
}