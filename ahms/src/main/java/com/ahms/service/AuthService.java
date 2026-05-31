package com.ahms.service;

import com.ahms.entity.*;
import com.ahms.repository.*;
import com.ahms.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Year;

@Service @RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final PatientRepository patientRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;
    private final AuthenticationManager authManager;
    private final UserDetailsService uds;
    private final QrService qrService;

    @Transactional
    public AuthResponse register(String username, String password, String email,
                                  String fullName, String phone, User.Role role) {
        if (userRepo.existsByUsername(username)) throw new RuntimeException("Username already taken");
        if (userRepo.existsByEmail(email))       throw new RuntimeException("Email already registered");

        User user = userRepo.save(User.builder()
                .username(username).password(encoder.encode(password))
                .email(email).fullName(fullName).phoneNumber(phone).role(role).build());

        if (role == User.Role.PATIENT) {
            String regId = String.format("AHMS-%d-%05d", Year.now().getValue(), patientRepo.count() + 1);
            Patient p = patientRepo.save(Patient.builder().user(user).registrationId(regId).build());
            try {
                String qrPath = qrService.generate(regId, fullName);
                p.setQrCodePath(qrPath);
                patientRepo.save(p);
            } catch (Exception ignored) {}
        }

        UserDetails ud = uds.loadUserByUsername(username);
        return new AuthResponse(jwt.generate(ud, role.name()), username, fullName, role.name(), user.getId());
    }

    public AuthResponse login(String username, String password) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        User user = userRepo.findByUsername(username).orElseThrow();
        UserDetails ud = uds.loadUserByUsername(username);
        return new AuthResponse(jwt.generate(ud, user.getRole().name()), username, user.getFullName(), user.getRole().name(), user.getId());
    }

    public record AuthResponse(String token, String username, String fullName, String role, Long userId) {}
}
