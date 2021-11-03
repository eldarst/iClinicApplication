package kg.iclinic.application.service;

import kg.iclinic.application.entity.Consultation;
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
    List<Consultation> getSortedOrders(Date dateForm, Date dateTo, String name);
    List<Consultation> findConsultationByPatientName(String patient);
}
