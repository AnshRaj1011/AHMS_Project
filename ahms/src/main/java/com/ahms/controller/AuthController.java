package com.ahms.controller;

import com.ahms.entity.User;
import com.ahms.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/auth") @CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterReq req) {
        var res = authService.register(req.username, req.password, req.email,
                req.fullName, req.phoneNumber, req.role);
        return ResponseEntity.ok(Map.of("success", true, "data", res));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReq req) {
        var res = authService.login(req.username, req.password);
        return ResponseEntity.ok(Map.of("success", true, "data", res));
    }

    @GetMapping("/ping")
    public ResponseEntity<?> ping() { return ResponseEntity.ok("AHMS API running ✓"); }

    @Data static class RegisterReq {
        @NotBlank String username;
        @NotBlank @Size(min=6) String password;
        @NotBlank @Email String email;
        @NotBlank String fullName;
        String phoneNumber;
        @NotNull User.Role role;
    }
    @Data static class LoginReq {
        @NotBlank String username;
        @NotBlank String password;
    }
}
