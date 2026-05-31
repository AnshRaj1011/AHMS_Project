package com.ahms.repository;
import com.ahms.entity.Appointment;
import com.ahms.entity.Doctor;
import com.ahms.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient(Patient patient);
    List<Appointment> findByDoctor(Doctor doctor);
    List<Appointment> findByAppointmentDate(LocalDate date);
    List<Appointment> findByDoctorAndAppointmentDate(Doctor doctor, LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.doctor=:d AND a.appointmentDate=:date AND a.appointmentTime=:time AND a.status <> 'CANCELLED'")
    List<Appointment> findConflicts(@Param("d") Doctor d, @Param("date") LocalDate date, @Param("time") LocalTime time);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate=:date AND a.status <> 'CANCELLED'")
    long countByDate(@Param("date") LocalDate date);
}
