package com.ahms.service;

import com.ahms.entity.*;
import com.ahms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service @RequiredArgsConstructor
public class LabTestService {
    private final LabTestRepository repo;
    private final PatientRepository patientRepo;

    private static final Map<LabTest.TestCategory, Double> PRICES = Map.of(
            LabTest.TestCategory.BLOOD_TEST, 300.0,   LabTest.TestCategory.CT_SCAN, 3500.0,
            LabTest.TestCategory.MRI,        6000.0,  LabTest.TestCategory.XRAY, 500.0,
            LabTest.TestCategory.URINE_TEST, 200.0,   LabTest.TestCategory.ECG, 400.0,
            LabTest.TestCategory.ULTRASOUND, 1200.0,  LabTest.TestCategory.OTHER, 500.0);

    @Transactional
    public LabTest book(Long patientId, String name, LabTest.TestCategory cat,
                         LocalDateTime scheduled, String orderedBy) {
        Patient p = patientRepo.findById(patientId).orElseThrow();
        return repo.save(LabTest.builder().patient(p).testName(name).testCategory(cat)
                .scheduledAt(scheduled).orderedBy(orderedBy)
                .price(PRICES.getOrDefault(cat, 500.0)).build());
    }

    public List<LabTest> getByPatient(Long pid)  { return repo.findByPatient(patientRepo.findById(pid).orElseThrow()); }
    public List<LabTest> getPending()             { return repo.findByStatus(LabTest.LabTestStatus.SCHEDULED); }
    public List<LabTest> getAll()                 { return repo.findAll(); }

    @Transactional
    public LabTest updateResult(Long id, String result) {
        LabTest t = repo.findById(id).orElseThrow();
        t.setResult(result); t.setStatus(LabTest.LabTestStatus.COMPLETED); return repo.save(t);
    }

    @Transactional
    public LabTest setStatus(Long id, LabTest.LabTestStatus status) {
        LabTest t = repo.findById(id).orElseThrow(); t.setStatus(status); return repo.save(t);
    }
}
