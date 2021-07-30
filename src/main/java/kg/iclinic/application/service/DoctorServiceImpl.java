package kg.iclinic.application.service;

import kg.iclinic.application.dao.DoctorRepository;
import kg.iclinic.application.entity.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorServiceImpl implements DoctorService{

    DoctorRepository doctorRepository;

    @Autowired
    public DoctorServiceImpl(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }


    @Override
    public Doctor findDoctor(String name) {
        Doctor doctor = doctorRepository.findByName(name);
        return doctor;
    }

    @Override
    public boolean doctorExists(String name) {
        return doctorRepository.existsByName(name);
    }

    @Override
    public void save(String name) {
        if(!doctorExists(name)) {
            Doctor doctor = new Doctor();
            doctor.setName(name);
            doctorRepository.save(doctor);
        }
    }

    @Override
    public Doctor findDoctorById(long id) {
        Doctor doctor = doctorRepository.getById(id);
        return doctor;
    }

    @Override
    public void findDoctor() {

    }
}
