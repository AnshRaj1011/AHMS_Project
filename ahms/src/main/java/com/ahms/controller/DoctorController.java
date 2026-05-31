package com.ahms.controller;

import com.ahms.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/doctors") @CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService svc;

    @GetMapping             public ResponseEntity<?> all()                            { return ok(svc.getAll()); }
    @GetMapping("/available") public ResponseEntity<?> available()                   { return ok(svc.getAvailable()); }
    @GetMapping("/{id}")    public ResponseEntity<?> byId(@PathVariable Long id)     {
        return svc.getById(id).map(d -> ok(d)).orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/specialization/{s}") public ResponseEntity<?> bySpec(@PathVariable String s) { return ok(svc.getBySpecialization(s)); }
    @GetMapping("/user/{uid}")         public ResponseEntity<?> byUser(@PathVariable Long uid)  {
        return svc.getByUserId(uid).map(d -> ok(d)).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        var d = svc.create(
                Long.parseLong(body.get("userId").toString()),
                (String) body.get("specialization"), (String) body.get("qualification"),
                (String) body.get("licenseNumber"),
                body.get("experienceYears") != null ? Integer.parseInt(body.get("experienceYears").toString()) : null,
                body.get("consultationFee") != null ? Double.parseDouble(body.get("consultationFee").toString()) : null,
                (String) body.get("availableDays"), (String) body.get("availableFrom"),
                (String) body.get("availableTo"), (String) body.get("cabinNumber"),
                (String) body.get("department"));
        return ok(d);
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<?> toggle(@PathVariable Long id) { return ok(svc.toggleAvailability(id)); }

    private ResponseEntity<?> ok(Object data) {
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
}
