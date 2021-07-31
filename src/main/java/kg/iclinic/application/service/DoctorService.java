package kg.iclinic.application.service;

import kg.iclinic.application.entity.Doctor;

import java.util.List;

public interface DoctorService {

    Doctor findDoctor(String name);
    boolean doctorExists(String name);
    void save(String name);
    Doctor findDoctorById(long id);

    void findDoctor();
    List<Doctor> findListOfDoctors();
}
