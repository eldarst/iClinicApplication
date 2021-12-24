package kg.iclinic.application.service;

import kg.iclinic.application.dao.TreatmentVisitRepository;
import kg.iclinic.application.entity.TreatmentPatient;
import kg.iclinic.application.entity.TreatmentVisit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class TreatmentVisitServiceImpl implements TreatmentVisitService{

    private final TreatmentVisitRepository treatmentVisitRepository;

    @Autowired
    public TreatmentVisitServiceImpl(TreatmentVisitRepository treatmentVisitRepository) {
        this.treatmentVisitRepository = treatmentVisitRepository;
    }

    @Override
    public Set<TreatmentVisit> getVisitsByDate(Date date) {
        return new HashSet<>(treatmentVisitRepository.findTreatmentVisitsByVisitDate(date));
    }

    @Override
    public Set<TreatmentVisit> getVisitsByDates(Date dateFrom, Date dateTo) {
        return new HashSet<>(treatmentVisitRepository.findTreatmentVisitsByVisitDateBetween(dateFrom, dateTo));
    }

    @Override
    public void delete(TreatmentVisit visit) {
        treatmentVisitRepository.delete(visit);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TreatmentVisit visit = treatmentVisitRepository.getById(id);
        treatmentVisitRepository.delete(visit);
    }

    @Override
    public void save(TreatmentVisit visit) {
        treatmentVisitRepository.save(visit);
    }

    @Override
    @Transactional
    public void update(TreatmentVisit visit) {
        treatmentVisitRepository.save(visit);
    }

    @Override
    public TreatmentVisit findTreatmentVisitById(Long id) {
        return treatmentVisitRepository.findById(id).get();
    }
}
