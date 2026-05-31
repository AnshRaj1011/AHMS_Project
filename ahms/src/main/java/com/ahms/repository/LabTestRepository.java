package com.ahms.repository;
import com.ahms.entity.LabTest;
import com.ahms.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LabTestRepository extends JpaRepository<LabTest, Long> {
    List<LabTest> findByPatient(Patient patient);
    List<LabTest> findByStatus(LabTest.LabTestStatus status);
}
