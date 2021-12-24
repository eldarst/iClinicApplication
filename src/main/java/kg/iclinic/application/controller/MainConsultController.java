package kg.iclinic.application.controller;

import kg.iclinic.application.entity.Consultation;
import kg.iclinic.application.entity.DoctorCons;
import kg.iclinic.application.service.ConsultationService;
import kg.iclinic.application.service.DoctorConsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cons")
public class MainConsultController {
    private final DoctorConsService doctorConsService;

    private final ConsultationService consultationService;

    private static final DateFormat dateFormat = new SimpleDateFormat(
            "MM/dd/yyyy", Locale.US);

    public MainConsultController(DoctorConsService doctorConsService, ConsultationService consultationService) {
        this.doctorConsService = doctorConsService;
        this.consultationService = consultationService;
    }

    @GetMapping("/listDoctors")
    public String getConsultationDocList(Model theModel) {
        theModel.addAttribute("theDoctor", new DoctorCons());
        theModel.addAttribute("theListOfDoctors", doctorConsService.findAllDoctors());

        return "list-doc-cons";
    }

    @GetMapping("/showFormForEditDoctor")
    public String showConsultationDocEditForm(@RequestParam("doctorId") long doctorId, Model theModel) {
        theModel.addAttribute("theDoctor", doctorConsService.findDoctor(doctorId));
        return "edit-consultations";
    }

    @PostMapping("/saveDoctor")
    public String AddConsultationDoctor(@ModelAttribute("theDoctor") DoctorCons theDoctor) throws ParseException {
        if (theDoctor != null) {
            doctorConsService.save(theDoctor);
        }
        return "redirect:/cons/listDoctors";
    }

    @GetMapping("/deleteDoctor")
    public String deleteDoctor(@RequestParam("doctorId") long doctorId) {
        doctorConsService.delete(doctorId);
        return "redirect:/cons/listDoctors";
    }

    Consultation lastSavedConsultation = new Consultation();

    @GetMapping("/listTodayOrders")
    public String getTodayConsultations(Model theModel) throws ParseException {
        theModel.addAttribute("theDate", dateFormat.parse(dateFormat.format(new Date())).toString());
        List<Consultation> todayOrders = consultationService.getTodayConsultations();
        theModel.addAttribute("theListOfConsultation", todayOrders);
        theModel.addAttribute("theDoctorList", doctorConsService.findAllDoctors());
        theModel.addAttribute("theConsultation", new Consultation());
        theModel.addAttribute("theDoctor", new DoctorCons());

        return "list-consultations";
    }

    @GetMapping("/showFormForEditConsultation")
    public String showProductEditConsultation(@RequestParam("consultationId") long consultationId, Model theModel) {
        theModel.addAttribute("theDoctorList", doctorConsService.findAllDoctors());
        theModel.addAttribute("theConsultation", consultationService.findConsultation(consultationId));
        return "edit-current-consultation";
    }

    @PostMapping("/saveConsultation")
    public String saveConsultation(@ModelAttribute("theConsultation") Consultation theConsultation) {
        if (theConsultation != null) {
            SetPreviousValues(theConsultation);
            consultationService.saveConsultation(theConsultation);
        }
        return "redirect:/cons/listTodayOrders";
    }

    private void SetPreviousValues(Consultation theConsultation) {
        List<Consultation> consultationsOfPatient = consultationService.findConsultationByPatientNameAndDoctor(theConsultation.getPatient(), theConsultation.getDoctor());
        Date today = new Date();
        theConsultation.setPreviousVisit(FindPreviousVisit(consultationsOfPatient, today));
        theConsultation.setConsultationDate(today);
        SetPrices(theConsultation);
    }

    private void SetPrices(Consultation theConsultation) {
        if (theConsultation.getConsultationDate().equals(theConsultation.getPreviousVisit())) {
            theConsultation.setPrice(theConsultation.getDoctor().getFirstCons());
        } else {
            theConsultation.setPrice(theConsultation.getDoctor().getRepCons());
        }

        theConsultation.setForClinic(theConsultation.getDoctor().getForClinic());
    }

    private Date FindPreviousVisit(List<Consultation> consultationsOfPatient, Date today) {
        return consultationsOfPatient.stream()
                .map(Consultation::getConsultationDate)
                .max(Date::compareTo)
                .orElse(today);
    }


    @GetMapping("/deleteConsultation")
    public String deleteConsultation(@RequestParam("consultationId") long consultationId) {
        consultationService.deleteConsultation(consultationId);
        return "redirect:/cons/listTodayOrders";
    }
}
