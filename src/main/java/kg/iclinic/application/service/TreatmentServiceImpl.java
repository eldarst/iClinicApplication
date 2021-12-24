package kg.iclinic.application.service;

import kg.iclinic.application.dao.TreatmentRepository;
import kg.iclinic.application.entity.Treatment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TreatmentServiceImpl implements TreatmentService {

    private final TreatmentRepository treatmentRepository;

    @Autowired
    public TreatmentServiceImpl(TreatmentRepository treatmentRepository) {
        this.treatmentRepository = treatmentRepository;
    }

    @Override
    public Treatment findTreatment(Long id) {
        return treatmentRepository.getById(id);
    }

    @Override
    public void save(Treatment treatment) {
        treatmentRepository.save(treatment);
    }

    @Override
    public List<Treatment> getProductList() {
        return treatmentRepository.findAll();
    }

    @Override
    public void delete(Long treatmentId) {
        Treatment treatment = treatmentRepository.getById(treatmentId);
        treatmentRepository.delete(treatment);
    }
}
