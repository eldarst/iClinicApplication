package kg.iclinic.application.service;

import kg.iclinic.application.entity.TreatmentPatient;
import kg.iclinic.application.entity.TreatmentVisit;

import java.util.Date;
import java.util.Set;

public interface TreatmentVisitService {
    Set<TreatmentVisit> getVisitsByDate(Date date);
    Set<TreatmentVisit> getVisitsByDates(Date dateFrom, Date dateTo);
    void delete(TreatmentVisit visit);
    void delete(Long id);
    void save(TreatmentVisit visit);
    void update(TreatmentVisit visit);
    TreatmentVisit findTreatmentVisitById(Long id);
}
