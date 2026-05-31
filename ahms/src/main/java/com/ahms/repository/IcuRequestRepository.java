package com.ahms.repository;
import com.ahms.entity.IcuRequest;
import com.ahms.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface IcuRequestRepository extends JpaRepository<IcuRequest, Long> {
    List<IcuRequest> findByPatient(Patient patient);
    List<IcuRequest> findByStatus(IcuRequest.RequestStatus status);
    List<IcuRequest> findAllByOrderByRequestedAtDesc();
}
