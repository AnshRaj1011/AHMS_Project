package com.ahms.controller;

import com.ahms.entity.Patient;
import com.ahms.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;

@RestController @RequestMapping("/patients") @CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService svc;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> all() { return ok(svc.getAll()); }

    @GetMapping("/{id}")
    public ResponseEntity<?> byId(@PathVariable Long id) {
        return svc.getById(id).map(p -> ok(p)).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reg/{regId}")
    public ResponseEntity<?> byRegId(@PathVariable String regId) {
        return svc.getByRegId(regId).map(p -> ok(p)).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> byUserId(@PathVariable Long userId) {
        return svc.getByUserId(userId).map(p -> ok(p)).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Patient p = svc.updateProfile(id,
                body.get("gender") != null ? Patient.Gender.valueOf((String) body.get("gender")) : null,
                body.get("dateOfBirth") != null ? LocalDate.parse((String) body.get("dateOfBirth")) : null,
                (String) body.get("bloodGroup"), (String) body.get("address"),
                (String) body.get("emergencyContact"), (String) body.get("medicalHistory"),
                (String) body.get("allergies"));
        return ok(p);
    }

    private ResponseEntity<?> ok(Object data) {
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
}
