package com.ahms.controller;

import com.ahms.entity.Appointment;
import com.ahms.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@RestController @RequestMapping("/appointments") @CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService svc;

    @GetMapping                        public ResponseEntity<?> all()                              { return ok(svc.getAll()); }
    @GetMapping("/today")              public ResponseEntity<?> today()                            { return ok(svc.getToday()); }
    @GetMapping("/patient/{pid}")      public ResponseEntity<?> byPatient(@PathVariable Long pid)  { return ok(svc.getByPatient(pid)); }
    @GetMapping("/doctor/{did}")       public ResponseEntity<?> byDoctor(@PathVariable Long did)   { return ok(svc.getByDoctor(did)); }

    @PostMapping
    public ResponseEntity<?> book(@RequestBody Map<String, Object> body) {
        var a = svc.book(
                Long.parseLong(body.get("patientId").toString()),
                Long.parseLong(body.get("doctorId").toString()),
                LocalDate.parse((String) body.get("appointmentDate")),
                LocalTime.parse((String) body.get("appointmentTime")),
                (String) body.get("reason"),
                body.get("type") != null ? Appointment.AppointmentType.valueOf((String) body.get("type")) : null);
        return ok(a);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> status(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ok(svc.updateStatus(id, Appointment.AppointmentStatus.valueOf(body.get("status"))));
    }

    @PatchMapping("/{id}/notes")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> notes(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ok(svc.addNotes(id, body.get("notes"), body.get("prescription")));
    }

    private ResponseEntity<?> ok(Object data) {
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
}
