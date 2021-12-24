package kg.iclinic.application.service;

import kg.iclinic.application.entity.Treatment;

import java.util.List;

public interface TreatmentService {

    Treatment findTreatment(Long id);

    void save(Treatment treatment) ;

    List<Treatment> getProductList();

    void delete(Long treatmentId);
}
