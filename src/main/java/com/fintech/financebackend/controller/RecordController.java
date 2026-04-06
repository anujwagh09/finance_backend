package com.fintech.financebackend.controller;

import com.fintech.financebackend.dto.request.CreateRecordRequest;
import com.fintech.financebackend.dto.request.UpdateRecordRequest;
import com.fintech.financebackend.dto.response.RecordResponse;
import com.fintech.financebackend.enums.RecordType;
import com.fintech.financebackend.service.RecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

	private final RecordService recordService;

	@GetMapping
	@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
	public ResponseEntity<Page<RecordResponse>> getAll(@RequestParam(required = false) RecordType type,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		PageRequest pageable = PageRequest.of(page, size, Sort.by("date").descending());
		return ResponseEntity.ok(recordService.getAll(type, category, from, to, pageable));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
	public ResponseEntity<RecordResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(recordService.getById(id));
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<RecordResponse> create(@Valid @RequestBody CreateRecordRequest request,
			@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.status(201).body(recordService.create(request, userDetails.getUsername()));
	}

	@PatchMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<RecordResponse> update(@PathVariable Long id,
			@Valid @RequestBody UpdateRecordRequest request) {
		return ResponseEntity.ok(recordService.update(id, request));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		recordService.softDelete(id);
		return ResponseEntity.noContent().build();
	}
}