package com.ahms.service;

import com.ahms.entity.*;
import com.ahms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor
public class IcuService {
    private final IcuRequestRepository repo;
    private final PatientRepository patientRepo;
    private final BedRepository bedRepo;

    @Transactional
    public IcuRequest requestIcu(Long patientId, String reason, String condition, IcuRequest.Priority priority) {
        Patient p = patientRepo.findById(patientId).orElseThrow();
        return repo.save(IcuRequest.builder().patient(p).reason(reason)
                .medicalCondition(condition).priority(priority).build());
    }

    public List<IcuRequest> getAll()         { return repo.findAllByOrderByRequestedAtDesc(); }
    public List<IcuRequest> getPending()     { return repo.findByStatus(IcuRequest.RequestStatus.PENDING); }
    public List<IcuRequest> getByPatient(Long pid) {
        return repo.findByPatient(patientRepo.findById(pid).orElseThrow());
    }

    @Transactional
    public IcuRequest approve(Long requestId, Long bedId, String adminNotes) {
        IcuRequest r = repo.findById(requestId).orElseThrow();
        Bed bed = bedRepo.findById(bedId).orElseThrow(() -> new RuntimeException("ICU bed not found"));
        if (bed.getStatus() != Bed.BedStatus.AVAILABLE)
            throw new RuntimeException("Selected ICU bed is not available");

        bed.setCurrentPatient(r.getPatient());
        bed.setStatus(Bed.BedStatus.OCCUPIED);
        bed.setAdmittedAt(LocalDateTime.now());
        bedRepo.save(bed);

        r.setStatus(IcuRequest.RequestStatus.APPROVED);
        r.setAssignedBed(bed);
        r.setAdminNotes(adminNotes);
        r.setResolvedAt(LocalDateTime.now());
        return repo.save(r);
    }

    @Transactional
    public IcuRequest reject(Long requestId, String adminNotes) {
        IcuRequest r = repo.findById(requestId).orElseThrow();
        r.setStatus(IcuRequest.RequestStatus.REJECTED);
        r.setAdminNotes(adminNotes);
        r.setResolvedAt(LocalDateTime.now());
        return repo.save(r);
    }
}
