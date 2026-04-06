package com.fintech.financebackend.controller;

import com.fintech.financebackend.dto.response.CategorySummary;
import com.fintech.financebackend.dto.response.MonthlyTrend;
import com.fintech.financebackend.dto.response.RecordResponse;
import com.fintech.financebackend.dto.response.SummaryResponse;
import com.fintech.financebackend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

	private final DashboardService dashboardService;

	@GetMapping("/summary")
	@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
	public ResponseEntity<SummaryResponse> getSummary() {
		return ResponseEntity.ok(dashboardService.getSummary());
	}

	@GetMapping("/categories")
	@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
	public ResponseEntity<List<CategorySummary>> getCategories() {
		return ResponseEntity.ok(dashboardService.getCategoryBreakdown());
	}

	@GetMapping("/trends")
	@PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
	public ResponseEntity<List<MonthlyTrend>> getTrends() {
		return ResponseEntity.ok(dashboardService.getMonthlyTrends());
	}

	@GetMapping("/recent")
	@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
	public ResponseEntity<List<RecordResponse>> getRecent(@RequestParam(defaultValue = "10") int limit) {
		return ResponseEntity.ok(dashboardService.getRecentActivity(limit));
	}
}