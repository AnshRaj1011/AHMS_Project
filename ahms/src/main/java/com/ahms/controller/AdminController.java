package com.ahms.controller;

import com.ahms.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/admin") @CrossOrigin(origins = "*")
@RequiredArgsConstructor @PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final PatientService patients;
    private final DoctorService doctors;
    private final AppointmentService appointments;
    private final BedService beds;
    private final AmbulanceService ambulance;
    private final LabTestService labTests;

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard() {
        Map<String, Object> stats = Map.of(
                "totalPatients",           patients.count(),
                "totalDoctors",            doctors.count(),
                "todayAppointments",       appointments.countToday(),
                "availableBeds",           beds.countAvailable(),
                "availableIcuBeds",        beds.countAvailableIcu(),
                "pendingAmbulance",        ambulance.getPending().size(),
                "pendingLabTests",         labTests.getPending().size()
        );
        return ResponseEntity.ok(Map.of("success", true, "data", stats));
    }

    @GetMapping("/patients")   public ResponseEntity<?> allPatients()   { return ok(patients.getAll()); }
    @GetMapping("/doctors")    public ResponseEntity<?> allDoctors()    { return ok(doctors.getAll()); }
    @GetMapping("/beds")       public ResponseEntity<?> allBeds()       { return ok(beds.getAll()); }
    @GetMapping("/ambulances") public ResponseEntity<?> allAmbulances() { return ok(ambulance.getAll()); }
    @GetMapping("/lab-tests")  public ResponseEntity<?> allLabTests()   { return ok(labTests.getAll()); }

    private ResponseEntity<?> ok(Object data) {
        return ResponseEntity.ok(Map.of("success", true, "data", data));
    }
}
