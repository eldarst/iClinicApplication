package kg.iclinic.application.entity;

import kg.iclinic.application.dao.DoctorConsRepository;
import kg.iclinic.application.service.DoctorConsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

public class DoctorConsConverter implements Converter<String, DoctorCons> {
    @Autowired
    DoctorConsService doctorConsService;

    @Override
    public DoctorCons convert(String source) {
        return doctorConsService.findDoctor(Long.parseLong(source));
    }
}
