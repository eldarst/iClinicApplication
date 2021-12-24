package kg.iclinic.application.service;

import kg.iclinic.application.dao.TreatmentPatientRepository;
import kg.iclinic.application.entity.TreatmentPatient;

import java.util.Set;

public interface TreatmentPatientService {
    Set<TreatmentPatient> GetActiveVisitedPatients();

    Set<TreatmentPatient> GetActiveNotVisitedPatients();

    Set<TreatmentPatient> GetAllPatients();

    TreatmentPatient findTreatmentPatientById(long patientId);

    void save(TreatmentPatient patient);

    void delete(Long id);

    void update(TreatmentPatient patient);
}
