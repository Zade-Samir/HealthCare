package com.healthcare.auth_service.service.impl;

import com.healthcare.auth_service.dto.AuthRequestDTO;
import com.healthcare.auth_service.dto.AuthResponseDTO;
import com.healthcare.auth_service.dto.LoginRequestDTO;
import com.healthcare.auth_service.entity.Role;
import com.healthcare.auth_service.entity.User;
import com.healthcare.auth_service.mapper.UserMapper;
import com.healthcare.auth_service.repository.UserRepository;
import com.healthcare.auth_service.service.AuthService;
import com.healthcare.auth_service.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
//@RequiredArgsConstructor //constructor of only required fields(not of doctorCode)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // Manual Constructor to use @Lazy
    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           @Lazy AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Value("${app.security.doctor-code}")
    private String doctorCode;

    @Value("${app.security.admin-code}")
    private String adminCode;

    @Override
    public void register(AuthRequestDTO authRequestDTO) {

        User user = new User();
        user.setUsername(authRequestDTO.getUsername());
        user.setEmail(authRequestDTO.getEmail());

        //encode the password
        user.setPassword(passwordEncoder.encode(authRequestDTO.getPassword()));

        //check the role of user
        if ("ADMIN".equalsIgnoreCase(authRequestDTO.getRole())) {
            if (!adminCode.equals(authRequestDTO.getSecurityCode())) {
                throw new RuntimeException("Unauthorized: Invalid Admin Security Code!");
            }
            user.setRole(Role.ADMIN);
        }

        else if ("DOCTOR".equalsIgnoreCase(authRequestDTO.getRole())) {
            if (!doctorCode.equals(authRequestDTO.getSecurityCode())) {
                throw new RuntimeException("Invalid Doctor Code!");
            }
            user.setRole(Role.DOCTOR);
        }

        else {
            user.setRole(Role.PATIENT);
        }
        userRepository.save(user);
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {

        // 1. Authenticate using Spring Security's AuthenticationManager
        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequestDTO.getIdentity(),
                                loginRequestDTO.getPassword()
                        )
                );

        // 2. If authenticated, generate token
        if(authentication.isAuthenticated()) {

            // Fetch user to get their role
            User user = userRepository.findByUsernameOrEmail(
                            loginRequestDTO.getIdentity(),
                            loginRequestDTO.getIdentity()
                    )
                    .orElseThrow(() -> new RuntimeException("User profile not found after authentication"));

            // Ensure role is prefixed with ROLE_ for consistency
            String role = "ROLE_" + user.getRole().name();

            String token = jwtUtil.generateToken(user.getUsername(), role);

            return new AuthResponseDTO(token, "Login Successful!");
        }
        else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}
