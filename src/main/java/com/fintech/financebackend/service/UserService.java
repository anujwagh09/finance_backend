package com.fintech.financebackend.service;

import com.fintech.financebackend.dto.request.UpdateUserRoleRequest;
import com.fintech.financebackend.dto.request.UpdateUserStatusRequest;
import com.fintech.financebackend.dto.response.UserResponse;
import com.fintech.financebackend.exception.ResourceNotFoundException;
import com.fintech.financebackend.model.User;
import com.fintech.financebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public List<UserResponse> getAllUsers() {
		return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
	}

	public UserResponse updateRole(Long id, UpdateUserRoleRequest request) {
		User user = findById(id);
		user.setRole(request.getRole());
		userRepository.save(user);
		return toResponse(user);
	}

	public UserResponse updateStatus(Long id, UpdateUserStatusRequest request) {
		User user = findById(id);
		user.setStatus(request.getStatus());
		userRepository.save(user);
		return toResponse(user);
	}

	public User findById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
	}

	private UserResponse toResponse(User user) {
		return new UserResponse(user.getId(), user.getEmail(), user.getRole(), user.getStatus(), user.getCreatedAt());
	}
}