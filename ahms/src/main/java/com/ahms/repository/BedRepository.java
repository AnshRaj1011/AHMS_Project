package com.ahms.repository;
import com.ahms.entity.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
public interface BedRepository extends JpaRepository<Bed, Long> {
    List<Bed> findByStatus(Bed.BedStatus status);
    List<Bed> findByBedTypeAndStatus(Bed.BedType type, Bed.BedStatus status);
    @Query("SELECT COUNT(b) FROM Bed b WHERE b.status='AVAILABLE'")        long countAvailable();
    @Query("SELECT COUNT(b) FROM Bed b WHERE b.bedType='ICU' AND b.status='AVAILABLE'") long countAvailableIcu();
}
