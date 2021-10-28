package kg.iclinic.application.service;

import kg.iclinic.application.entity.DoctorCons;

import java.util.List;

public interface DoctorConsService {
    void save(DoctorCons doctor);
    DoctorCons findDoctor(Long id);
    void delete(Long id);
    List<DoctorCons> findAllDoctors();
}
