package com.ahms.controller;

import com.ahms.entity.LabTest;
import com.ahms.service.LabTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController @RequestMapping("/lab-tests") @CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LabTestController {
    private final LabTestService svc;

    @GetMapping              public ResponseEntity<?> all()                                   { return ok(svc.getAll()); }
    @GetMapping("/pending")  public ResponseEntity<?> pending()                              { return ok(svc.getPending()); }
    @GetMapping("/patient/{pid}") public ResponseEntity<?> byPatient(@PathVariable Long pid) { return ok(svc.getByPatient(pid)); }

    @PostMapping
    public ResponseEntity<?> book(@RequestBody Map<String, Object> body) {
        return ok(svc.book(
                Long.parseLong(body.get("patientId").toString()),
                (String) body.get("testName"),
                LabTest.TestCategory.valueOf((String) body.get("testCategory")),
                LocalDateTime.parse((String) body.get("scheduledAt")),
                (String) body.get("orderedBy")));
    }

    @PatchMapping("/{id}/result")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<?> result(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ok(svc.updateResult(id, body.get("result")));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<?> status(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ok(svc.setStatus(id, LabTest.LabTestStatus.valueOf(body.get("status"))));
    }

    private ResponseEntity<?> ok(Object data) {
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
}
