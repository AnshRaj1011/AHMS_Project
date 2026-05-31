package com.ahms.controller;

import com.ahms.entity.IcuRequest;
import com.ahms.service.IcuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/icu") @CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class IcuController {
    private final IcuService svc;

    @GetMapping                        public ResponseEntity<?> all()                                    { return ok(svc.getAll()); }
    @GetMapping("/pending")            public ResponseEntity<?> pending()                               { return ok(svc.getPending()); }
    @GetMapping("/patient/{pid}")      public ResponseEntity<?> byPatient(@PathVariable Long pid)        { return ok(svc.getByPatient(pid)); }

    @PostMapping("/request")
    public ResponseEntity<?> request(@RequestBody Map<String, Object> body) {
        return ok(svc.requestIcu(
                Long.parseLong(body.get("patientId").toString()),
                (String) body.get("reason"),
                (String) body.get("medicalCondition"),
                body.get("priority") != null ? IcuRequest.Priority.valueOf((String) body.get("priority")) : IcuRequest.Priority.HIGH));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','EMERGENCY_STAFF')")
    public ResponseEntity<?> approve(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return ok(svc.approve(id, Long.parseLong(body.get("bedId").toString()), (String) body.get("adminNotes")));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','EMERGENCY_STAFF')")
    public ResponseEntity<?> reject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ok(svc.reject(id, body.get("adminNotes")));
    }

    private ResponseEntity<?> ok(Object data) {
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
}
