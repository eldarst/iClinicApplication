package kg.iclinic.application.service;

import kg.iclinic.application.dao.DoctorConsRepository;
import kg.iclinic.application.entity.DoctorCons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorConsServiceImpl implements DoctorConsService{

    DoctorConsRepository doctorConsRepository;

    @Autowired
    public DoctorConsServiceImpl(DoctorConsRepository doctorConsRepository) {
        this.doctorConsRepository = doctorConsRepository;
    }

    @Override
    public void save(DoctorCons doctor) {
        doctorConsRepository.save(doctor);
    }

    @Override
    public DoctorCons findDoctor(Long id) {
        return doctorConsRepository.getById(id);
    }

    @Override
    public void delete(Long id) {
        if(doctorConsRepository.existsById(id))
        {
            DoctorCons doctor = doctorConsRepository.getById(id);
            doctorConsRepository.delete(doctor);
        }
    }

    @Override
    public List<DoctorCons> findAllDoctors() {
        return doctorConsRepository.findAll();
    }
}
