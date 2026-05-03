package com.healthcare.auth_service.service;

import com.healthcare.auth_service.dto.AuthRequestDTO;
import com.healthcare.auth_service.dto.AuthResponseDTO;
import com.healthcare.auth_service.dto.LoginRequestDTO;
import jakarta.validation.Valid;

public interface AuthService {
    void register(@Valid AuthRequestDTO authRequestDTO);

    AuthResponseDTO login(@Valid LoginRequestDTO loginRequestDTO);
}
