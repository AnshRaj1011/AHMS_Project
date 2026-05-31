package com.ahms.service;

import com.ahms.entity.*;
import com.ahms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service @RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repo;
    private final UserRepository userRepo;

    public List<Patient> getAll()                         { return repo.findAll(); }
    public Optional<Patient> getById(Long id)             { return repo.findById(id); }
    public Optional<Patient> getByRegId(String regId)     { return repo.findByRegistrationId(regId); }
    public Optional<Patient> getByUserId(Long userId)     {
        return userRepo.findById(userId).flatMap(repo::findByUser);
    }

    @Transactional
    public Patient updateProfile(Long id, Patient.Gender gender, LocalDate dob,
                                  String blood, String address, String emergencyContact,
                                  String history, String allergies) {
        Patient p = repo.findById(id).orElseThrow(() -> new RuntimeException("Patient not found"));
        if (gender != null)          p.setGender(gender);
        if (dob != null)             p.setDateOfBirth(dob);
        if (blood != null)           p.setBloodGroup(blood);
        if (address != null)         p.setAddress(address);
        if (emergencyContact != null) p.setEmergencyContact(emergencyContact);
        if (history != null)         p.setMedicalHistory(history);
        if (allergies != null)       p.setAllergies(allergies);
        return repo.save(p);
    }

    public long count() { return repo.count(); }
}
