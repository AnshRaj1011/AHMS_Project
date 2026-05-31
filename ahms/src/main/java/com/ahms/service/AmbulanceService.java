package com.ahms.service;

import com.ahms.entity.*;
import com.ahms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor
public class AmbulanceService {
    private final AmbulanceRequestRepository repo;

    @Transactional
    public AmbulanceRequest request(String name, String phone, String address,
                                     Double lat, Double lng, String emergencyType, String notes) {
        return repo.save(AmbulanceRequest.builder().requesterName(name).requesterPhone(phone)
                .pickupAddress(address).latitude(lat).longitude(lng)
                .emergencyType(emergencyType).notes(notes).build());
    }

    public List<AmbulanceRequest> getPending()  { return repo.findByStatus(AmbulanceRequest.RequestStatus.PENDING); }
    public List<AmbulanceRequest> getAll()       { return repo.findAllByOrderByRequestedAtDesc(); }

    @Transactional
    public AmbulanceRequest dispatch(Long id, String driver, String ambulanceNum, Integer eta) {
        AmbulanceRequest r = repo.findById(id).orElseThrow();
        r.setAssignedDriver(driver); r.setAmbulanceNumber(ambulanceNum);
        r.setEstimatedArrival(eta); r.setStatus(AmbulanceRequest.RequestStatus.DISPATCHED);
        r.setDispatchedAt(LocalDateTime.now());
        return repo.save(r);
    }

    @Transactional
    public AmbulanceRequest setStatus(Long id, AmbulanceRequest.RequestStatus status) {
        AmbulanceRequest r = repo.findById(id).orElseThrow(); r.setStatus(status); return repo.save(r);
    }
}
