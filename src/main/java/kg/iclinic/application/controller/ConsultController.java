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

@Controller
@RequestMapping("/cons")
public class ConsultController {
    @Autowired
    DoctorConsService doctorConsService;

    @Autowired
    ConsultationService consultationService;

    private static final DateFormat dateFormat = new SimpleDateFormat(
            "MM/dd/yyyy", Locale.US);

    @GetMapping("/listDoctors")
    public String getProductList(Model theModel) {
        theModel.addAttribute("theDoctor", new DoctorCons());
        theModel.addAttribute("theListOfDoctors", doctorConsService.findAllDoctors());

        return "list-doc-cons";
    }

    @GetMapping("/showFormForEditDoctor")
    public String showProductEditForm(@RequestParam("doctorId") long doctorId, Model theModel) {
        theModel.addAttribute("theDoctor", doctorConsService.findDoctor(doctorId));
        return "edit-consultations";
    }

    @PostMapping("/saveDoctor")
    public String AddProduct(@ModelAttribute("theDoctor") DoctorCons theDoctor) throws ParseException {
        if (theDoctor != null) {
            doctorConsService.save(theDoctor);
        }
        return "redirect:/cons/listDoctors";
    }

    @GetMapping("/deleteDoctor")
    public String deleteProduct(@RequestParam("doctorId") long doctorId) {
        doctorConsService.delete(doctorId);
        return "redirect:/cons/listDoctors";
    }

    Consultation lastSavedConsultation = new Consultation();
    @GetMapping("/listTodayOrders")
    public String getTodayOrders(Model theModel) throws ParseException {
        theModel.addAttribute("theDate", dateFormat.parse(dateFormat.format(new Date())).toString());
        List<Consultation> todayOrders = consultationService.getTodayConsultations();
        theModel.addAttribute("theListOfConsultation", todayOrders);
        theModel.addAttribute("theDoctorList", doctorConsService.findAllDoctors());
        theModel.addAttribute("theConsultation", new Consultation());

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
            Date today = new Date();
            theConsultation.setConsultationDate(today);
            consultationService.saveConsultation(theConsultation);
        }
        return "redirect:/cons/listTodayOrders";
    }

    @GetMapping("/deleteConsultation")
    public String deleteConsultation(@RequestParam("consultationId") long consultationId) {
        consultationService.deleteConsultation(consultationId);
        return "redirect:/cons/listTodayOrders";
    }
}
