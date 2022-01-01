package kg.iclinic.application.service;

import kg.iclinic.application.dao.ConsultationRepository;
import kg.iclinic.application.entity.Consultation;
import kg.iclinic.application.entity.DoctorCons;
import kg.iclinic.application.model.Methods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
public class ConsultationServiceImpl implements ConsultationService{

    ConsultationRepository consultationRepository;

    @Autowired
    public ConsultationServiceImpl(ConsultationRepository consultationRepository) {
        this.consultationRepository = consultationRepository;
    }

    @Override
    public void saveConsultation(Consultation theConsultation) {
        consultationRepository.save(theConsultation);
    }

    @Override
    public Consultation findConsultation(Long consultationId) {
        return consultationRepository.getById(consultationId);
    }

    @Override
    public List<Consultation> getTodayConsultations() throws ParseException {
        Date today = Methods.getTodaysDate();
        return consultationRepository.findByConsultationDate(today);
    }

    @Override
    public List<Consultation> getConsultationsByDay(Date date) {
        return consultationRepository.findByConsultationDate(date);
    }

    @Override
    public void deleteConsultation(Long consultationId) {
        Consultation consultation = consultationRepository.getById(consultationId);
        consultationRepository.delete(consultation);
    }

    @Override
    public List<Consultation> getConsultationsBetweenDates(Date dateForm, Date dateTo) {
        return consultationRepository.findByConsultationDateBetween(dateForm, dateTo);
    }

    @Override
    public List<Consultation> getDoctorConsultationsBetweenDates(Date dateFrom, Date dateTo, DoctorCons doctor) {
        if (doctor != null)
            return consultationRepository.findByConsultationDateBetweenAndDoctor(dateFrom, dateTo, doctor);
        return consultationRepository.findByConsultationDateBetween(dateFrom, dateTo);
    }

    @Override
    public List<Consultation> findConsultationByPatientNameAndDoctor(String patient, DoctorCons doctor) {
        return consultationRepository.findByPatientAndAndDoctor(patient, doctor);
    }
}
