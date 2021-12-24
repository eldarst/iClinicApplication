package kg.iclinic.application.dao;

import kg.iclinic.application.entity.TreatmentPatient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface TreatmentPatientRepository extends JpaRepository<TreatmentPatient, Long> {
    List<TreatmentPatient> findByIsActiveAndLastVisit(boolean isActive, Date date);

    List<TreatmentPatient> findByIsActiveAndLastVisitBefore(boolean isActive, Date date);

    List<TreatmentPatient> findByOrderByLastVisit();
}
