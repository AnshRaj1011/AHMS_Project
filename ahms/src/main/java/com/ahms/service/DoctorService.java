package com.ahms.service;

import com.ahms.entity.*;
import com.ahms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service @RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository repo;
    private final UserRepository userRepo;

    public List<Doctor> getAll()                                          { return repo.findAll(); }
    public List<Doctor> getAvailable()                                    { return repo.findByIsAvailableTrue(); }
    public List<Doctor> getBySpecialization(String s)                     { return repo.findBySpecialization(s); }
    public Optional<Doctor> getById(Long id)                              { return repo.findById(id); }
    public Optional<Doctor> getByUserId(Long uid)                         {
        return userRepo.findById(uid).flatMap(repo::findByUser);
    }

    @Transactional
    public Doctor create(Long userId, String specialization, String qualification,
                          String license, Integer exp, Double fee, String days,
                          String from, String to, String cabin, String dept) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return repo.save(Doctor.builder().user(user).specialization(specialization)
                .qualification(qualification).licenseNumber(license)
                .experienceYears(exp).consultationFee(fee)
                .availableDays(days).availableFrom(from).availableTo(to)
                .cabinNumber(cabin).department(dept).isAvailable(true).build());
    }

    @Transactional
    public Doctor toggleAvailability(Long id) {
        Doctor d = repo.findById(id).orElseThrow();
        d.setIsAvailable(!d.getIsAvailable());
        return repo.save(d);
    }

    public long count() { return repo.count(); }
}
