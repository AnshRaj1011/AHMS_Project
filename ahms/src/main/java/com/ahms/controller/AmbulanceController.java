package com.ahms.controller;

import com.ahms.entity.AmbulanceRequest;
import com.ahms.service.AmbulanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/ambulance") @CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AmbulanceController {
    private final AmbulanceService svc;

    @GetMapping           public ResponseEntity<?> all()                          { return ok(svc.getAll()); }
    @GetMapping("/pending") public ResponseEntity<?> pending()                   { return ok(svc.getPending()); }

    @PostMapping("/public/request")
    public ResponseEntity<?> request(@RequestBody Map<String, Object> body) {
        return ok(svc.request(
                (String) body.get("requesterName"), (String) body.get("requesterPhone"),
                (String) body.get("pickupAddress"),
                body.get("latitude") != null ? Double.parseDouble(body.get("latitude").toString()) : null,
                body.get("longitude") != null ? Double.parseDouble(body.get("longitude").toString()) : null,
                (String) body.get("emergencyType"), (String) body.get("notes")));
    }

    @PatchMapping("/dispatch/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMERGENCY_STAFF')")
    public ResponseEntity<?> dispatch(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return ok(svc.dispatch(id, (String) body.get("driverName"),
                (String) body.get("ambulanceNumber"),
                body.get("eta") != null ? Integer.parseInt(body.get("eta").toString()) : null));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','EMERGENCY_STAFF')")
    public ResponseEntity<?> status(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ok(svc.setStatus(id, AmbulanceRequest.RequestStatus.valueOf(body.get("status"))));
    }

    private ResponseEntity<?> ok(Object data) {
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
}
