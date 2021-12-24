package kg.iclinic.application.dao;

import kg.iclinic.application.entity.TreatmentPatient;
import kg.iclinic.application.entity.TreatmentVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TreatmentVisitRepository extends JpaRepository<TreatmentVisit, Long> {

    List<TreatmentVisit> findTreatmentVisitsByVisitDate(Date visit);

    List<TreatmentVisit> findTreatmentVisitsByVisitDateBetween(Date dateFrom, Date dateTo);
}
