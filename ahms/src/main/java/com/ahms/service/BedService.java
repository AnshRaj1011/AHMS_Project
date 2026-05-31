package com.ahms.service;

import com.ahms.entity.*;
import com.ahms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor
public class BedService {
    private final BedRepository repo;
    private final PatientRepository patientRepo;

    public List<Bed> getAll()                                   { return repo.findAll(); }
    public List<Bed> getAvailable()                             { return repo.findByStatus(Bed.BedStatus.AVAILABLE); }
    public List<Bed> getAvailableByType(Bed.BedType type)       { return repo.findByBedTypeAndStatus(type, Bed.BedStatus.AVAILABLE); }

    @Transactional
    public Bed admit(Long bedId, Long patientId) {
        Bed bed = repo.findById(bedId).orElseThrow(() -> new RuntimeException("Bed not found"));
        if (bed.getStatus() != Bed.BedStatus.AVAILABLE) throw new RuntimeException("Bed is not available");
        bed.setCurrentPatient(patientRepo.findById(patientId).orElseThrow());
        bed.setStatus(Bed.BedStatus.OCCUPIED);
        bed.setAdmittedAt(LocalDateTime.now());
        return repo.save(bed);
    }

    @Transactional
    public Bed discharge(Long bedId) {
        Bed bed = repo.findById(bedId).orElseThrow();
        bed.setCurrentPatient(null);
        bed.setStatus(Bed.BedStatus.AVAILABLE);
        bed.setAdmittedAt(null);
        return repo.save(bed);
    }

    @Transactional
    public Bed addBed(String num, String ward, Bed.BedType type, Integer floor, Double rate) {
        return repo.save(Bed.builder().bedNumber(num).wardName(ward).bedType(type)
                .floorNumber(floor).dailyRate(rate).build());
    }

    @Transactional
    public Bed setStatus(Long id, Bed.BedStatus status) {
        Bed b = repo.findById(id).orElseThrow(); b.setStatus(status); return repo.save(b);
    }

    public long countAvailable()    { return repo.countAvailable(); }
    public long countAvailableIcu() { return repo.countAvailableIcu(); }
}
