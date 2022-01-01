package kg.iclinic.application.service;

import kg.iclinic.application.entity.Consultation;
import kg.iclinic.application.entity.DoctorCons;
import kg.iclinic.application.entity.Order;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface ConsultationService {
    void saveConsultation(Consultation theConsultation);
    Consultation findConsultation(Long consultationId);
    List<Consultation> getTodayConsultations() throws ParseException;
    List<Consultation> getConsultationsByDay(Date date);
    void deleteConsultation(Long consultationId);
    List<Consultation> getConsultationsBetweenDates(Date dateForm, Date dateTo);
    List<Consultation> getDoctorConsultationsBetweenDates(Date dateFrom, Date dateTo, DoctorCons doctor);
    List<Consultation> findConsultationByPatientNameAndDoctor(String patient, DoctorCons doctor);
}
