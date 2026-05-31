package com.ahms.repository;
import com.ahms.entity.Patient;
import com.ahms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUser(User user);
    Optional<Patient> findByRegistrationId(String registrationId);
}
