package com.ahms.repository;
import com.ahms.entity.AmbulanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AmbulanceRequestRepository extends JpaRepository<AmbulanceRequest, Long> {
    List<AmbulanceRequest> findByStatus(AmbulanceRequest.RequestStatus status);
    List<AmbulanceRequest> findAllByOrderByRequestedAtDesc();
}
