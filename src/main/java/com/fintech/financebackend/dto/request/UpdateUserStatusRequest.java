package com.fintech.financebackend.dto.request;

import com.fintech.financebackend.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {

    @NotNull(message = "Status is required")
    private UserStatus status;
}