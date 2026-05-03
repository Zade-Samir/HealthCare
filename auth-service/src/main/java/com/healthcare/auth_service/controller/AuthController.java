package com.healthcare.auth_service.controller;

import com.healthcare.auth_service.common.ApiResponse;
import com.healthcare.auth_service.dto.AuthRequestDTO;
import com.healthcare.auth_service.dto.AuthResponseDTO;
import com.healthcare.auth_service.dto.LoginRequestDTO;
import com.healthcare.auth_service.service.AuthService;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    //register the user
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody AuthRequestDTO authRequestDTO
            ) {
        authService.register(authRequestDTO);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "User is registered successfully!",
                        "registration is completed"
                )
        );
    }

    //login the user
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO
            ) {
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "logging successful!",
                        authService.login(loginRequestDTO)
                )
        );
    }
}
