package com.fintech.financebackend.controller;

import com.fintech.financebackend.dto.request.UpdateUserRoleRequest;
import com.fintech.financebackend.dto.request.UpdateUserStatusRequest;
import com.fintech.financebackend.dto.response.UserResponse;
import com.fintech.financebackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<UserResponse>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}

	@PatchMapping("/{id}/role")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserResponse> updateRole(@PathVariable Long id,
			@Valid @RequestBody UpdateUserRoleRequest request) {
		return ResponseEntity.ok(userService.updateRole(id, request));
	}

	@PatchMapping("/{id}/status")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserResponse> updateStatus(@PathVariable Long id,
			@Valid @RequestBody UpdateUserStatusRequest request) {
		return ResponseEntity.ok(userService.updateStatus(id, request));
	}
}