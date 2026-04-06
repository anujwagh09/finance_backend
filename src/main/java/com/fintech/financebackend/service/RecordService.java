package com.fintech.financebackend.service;

import com.fintech.financebackend.dto.request.CreateRecordRequest;
import com.fintech.financebackend.dto.request.UpdateRecordRequest;
import com.fintech.financebackend.dto.response.RecordResponse;
import com.fintech.financebackend.enums.RecordType;
import com.fintech.financebackend.exception.ResourceNotFoundException;
import com.fintech.financebackend.model.FinancialRecord;
import com.fintech.financebackend.model.User;
import com.fintech.financebackend.repository.FinancialRecordRepository;
import com.fintech.financebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RecordService {

	private final FinancialRecordRepository recordRepository;
	private final UserRepository userRepository;

	public Page<RecordResponse> getAll(RecordType type, String category, LocalDate from, LocalDate to,
			Pageable pageable) {
		return recordRepository.findAllFiltered(type, category, from, to, pageable).map(this::toResponse);
	}

	public RecordResponse getById(Long id) {
		FinancialRecord record = recordRepository.findActiveById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));
		return toResponse(record);
	}

	public RecordResponse create(CreateRecordRequest request, String userEmail) {
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		FinancialRecord record = FinancialRecord.builder().amount(request.getAmount()).type(request.getType())
				.category(request.getCategory()).date(request.getDate()).notes(request.getNotes()).createdBy(user)
				.build();

		return toResponse(recordRepository.save(record));
	}

	public RecordResponse update(Long id, UpdateRecordRequest request) {
		FinancialRecord record = recordRepository.findActiveById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));

		// Only update fields that are provided
		if (request.getAmount() != null)
			record.setAmount(request.getAmount());
		if (request.getType() != null)
			record.setType(request.getType());
		if (request.getCategory() != null)
			record.setCategory(request.getCategory());
		if (request.getDate() != null)
			record.setDate(request.getDate());
		if (request.getNotes() != null)
			record.setNotes(request.getNotes());

		return toResponse(recordRepository.save(record));
	}

	public void softDelete(Long id) {
		FinancialRecord record = recordRepository.findActiveById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + id));

		record.setDeletedAt(LocalDateTime.now());
		recordRepository.save(record);
	}

	private RecordResponse toResponse(FinancialRecord record) {
		return new RecordResponse(record.getId(), record.getAmount(), record.getType(), record.getCategory(),
				record.getDate(), record.getNotes(), record.getCreatedBy().getEmail(), record.getCreatedAt());
	}
}