package kg.iclinic.application.service;

import kg.iclinic.application.dao.TreatmentPatientRepository;
import kg.iclinic.application.entity.TreatmentPatient;
import kg.iclinic.application.model.Methods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class TreatmentPatientServiceImpl implements TreatmentPatientService {

    @Autowired
    private TreatmentPatientRepository treatmentPatientRepository;

    @Override
    public Set<TreatmentPatient> GetActiveVisitedPatients() {
        Date today = Methods.getTodaysDate();
        return new HashSet<>(treatmentPatientRepository.findByIsActiveAndLastVisit(true, today));
    }

    @Override
    public Set<TreatmentPatient> GetActiveNotVisitedPatients() {
        Date today = Methods.getTodaysDate();
        Set<TreatmentPatient> result = new HashSet<>(treatmentPatientRepository.findByIsActiveAndLastVisitBefore(true, today));
        result.addAll(treatmentPatientRepository.findByIsActiveAndLastVisit(true, null));
        return result;

    }

    @Override
    public Set<TreatmentPatient> GetAllPatients() {
        return new HashSet<>(treatmentPatientRepository.findByOrderByLastVisit());
    }

    @Override
    public TreatmentPatient findTreatmentPatientById(long patientId) {
        return treatmentPatientRepository.findById(patientId).get();
    }

    @Override
    public void save(TreatmentPatient patient) {
        treatmentPatientRepository.save(patient);
    }

    @Override
    public void delete(Long id) {
        TreatmentPatient patient = treatmentPatientRepository.getById(id);
        treatmentPatientRepository.delete(patient);
    }

    @Override
    @Transactional
    public void update(TreatmentPatient patient) {
        TreatmentPatient pat = treatmentPatientRepository.save(patient);
    }
}
