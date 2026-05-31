package com.ahms.controller;

import com.ahms.entity.Bed;
import com.ahms.service.BedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/beds") @CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BedController {
    private final BedService svc;

    @GetMapping                          public ResponseEntity<?> all()                                { return ok(svc.getAll()); }
    @GetMapping("/available")            public ResponseEntity<?> available()                         { return ok(svc.getAvailable()); }
    @GetMapping("/available/icu")        public ResponseEntity<?> availableIcu()                      { return ok(svc.getAvailableByType(Bed.BedType.ICU)); }
    @GetMapping("/available/{type}")     public ResponseEntity<?> byType(@PathVariable String type)   {
        return ok(svc.getAvailableByType(Bed.BedType.valueOf(type.toUpperCase())));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> add(@RequestBody Map<String, Object> body) {
        return ok(svc.addBed((String) body.get("bedNumber"), (String) body.get("wardName"),
                Bed.BedType.valueOf((String) body.get("bedType")),
                body.get("floorNumber") != null ? Integer.parseInt(body.get("floorNumber").toString()) : null,
                body.get("dailyRate") != null ? Double.parseDouble(body.get("dailyRate").toString()) : null));
    }

    @PatchMapping("/{id}/admit")
    @PreAuthorize("hasAnyRole('ADMIN','EMERGENCY_STAFF')")
    public ResponseEntity<?> admit(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        return ok(svc.admit(id, body.get("patientId")));
    }

    @PatchMapping("/{id}/discharge")
    @PreAuthorize("hasAnyRole('ADMIN','EMERGENCY_STAFF')")
    public ResponseEntity<?> discharge(@PathVariable Long id) { return ok(svc.discharge(id)); }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','EMERGENCY_STAFF')")
    public ResponseEntity<?> status(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ok(svc.setStatus(id, Bed.BedStatus.valueOf(body.get("status"))));
    }

    private ResponseEntity<?> ok(Object data) {
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
}
