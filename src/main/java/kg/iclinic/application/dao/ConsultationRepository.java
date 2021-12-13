package kg.iclinic.application.dao;

import kg.iclinic.application.entity.Consultation;
import kg.iclinic.application.entity.DoctorCons;
import kg.iclinic.application.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByConsultationDate(Date now);
    List<Consultation> findByConsultationDateBetween(Date start, Date end);
    List<Consultation> findByPatientAndAndDoctor(String patient, DoctorCons doctor);
}
