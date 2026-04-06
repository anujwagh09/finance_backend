package com.fintech.financebackend.service;

import com.fintech.financebackend.dto.response.CategorySummary;
import com.fintech.financebackend.dto.response.MonthlyTrend;
import com.fintech.financebackend.dto.response.RecordResponse;
import com.fintech.financebackend.dto.response.SummaryResponse;
import com.fintech.financebackend.enums.RecordType;
import com.fintech.financebackend.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

	private final FinancialRecordRepository recordRepository;

	public SummaryResponse getSummary() {
		List<Object[]> totals = recordRepository.totalsByType();

		BigDecimal totalIncome = BigDecimal.ZERO;
		BigDecimal totalExpense = BigDecimal.ZERO;

		for (Object[] row : totals) {
			RecordType type = (RecordType) row[0];
			BigDecimal amount = (BigDecimal) row[1];
			if (type == RecordType.INCOME) {
				totalIncome = amount;
			} else {
				totalExpense = amount;
			}
		}

		BigDecimal netBalance = totalIncome.subtract(totalExpense);
		long totalRecords = recordRepository.countActive();

		return new SummaryResponse(totalIncome, totalExpense, netBalance, totalRecords);
	}

	public List<CategorySummary> getCategoryBreakdown() {
		return recordRepository.categoryBreakdown().stream()
				.map(row -> new CategorySummary((String) row[0], (RecordType) row[1], (BigDecimal) row[2]))
				.collect(Collectors.toList());
	}

	public List<MonthlyTrend> getMonthlyTrends() {
		List<Object[]> rows = recordRepository.monthlyTrends();
		List<MonthlyTrend> trends = new ArrayList<>();

		java.util.Map<String, MonthlyTrend> map = new java.util.LinkedHashMap<>();

		for (Object[] row : rows) {
			String month = (String) row[0];
			String type = (String) row[1];
			BigDecimal total = new BigDecimal(row[2].toString());

			map.putIfAbsent(month, new MonthlyTrend(month, BigDecimal.ZERO, BigDecimal.ZERO));

			MonthlyTrend trend = map.get(month);
			if (type.equals("INCOME")) {
				trend.setIncome(total);
			} else {
				trend.setExpense(total);
			}
		}

		return new ArrayList<>(map.values());
	}

	public List<RecordResponse> getRecentActivity(int limit) {
		return recordRepository.findRecent(PageRequest.of(0, limit)).stream()
				.map(record -> new RecordResponse(record.getId(), record.getAmount(), record.getType(),
						record.getCategory(), record.getDate(), record.getNotes(), record.getCreatedBy().getEmail(),
						record.getCreatedAt()))
				.collect(Collectors.toList());
	}
}