package com.ahms.repository;
import com.ahms.entity.Doctor;
import com.ahms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUser(User user);
    List<Doctor> findBySpecialization(String specialization);
    List<Doctor> findByIsAvailableTrue();
    List<Doctor> findByDepartment(String department);
}
