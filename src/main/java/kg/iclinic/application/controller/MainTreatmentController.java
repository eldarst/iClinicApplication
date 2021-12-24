package kg.iclinic.application.controller;

import kg.iclinic.application.entity.Treatment;
import kg.iclinic.application.entity.TreatmentPatient;
import kg.iclinic.application.entity.TreatmentVisit;
import kg.iclinic.application.service.TreatmentPatientService;
import kg.iclinic.application.service.TreatmentService;
import kg.iclinic.application.service.TreatmentVisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@Controller
@RequestMapping("/trt")
public class MainTreatmentController {

    @Autowired
    private TreatmentService treatmentService;

    @Autowired
    private TreatmentVisitService treatmentVisitService;

    @Autowired
    private TreatmentPatientService treatmentPatientService;


    @GetMapping("/listActivePatients")
    public String getActivePatients(Model theModel) {
        theModel.addAttribute("thePatient", new TreatmentPatient());
        theModel.addAttribute("theListOfPatients", treatmentPatientService.GetActiveNotVisitedPatients());
        theModel.addAttribute("theListOfMarkedPatients", treatmentPatientService.GetActiveVisitedPatients());
        theModel.addAttribute("theTreatmentList", treatmentService.getProductList());
        theModel.addAttribute("payment", 0);
        return "list-treatment-patients";
    }

    @GetMapping("/markToday")
    public String markVisit(@ModelAttribute("patientId") Long id) {
        TreatmentPatient patient = treatmentPatientService.findTreatmentPatientById(id);
        if (patient != null) {
            Date today = new Date();
            TreatmentVisit visit = new TreatmentVisit(patient,today);
            patient.addVisit(visit);
            patient.setLastVisit(today);
            treatmentVisitService.update(visit);
            treatmentPatientService.update(patient);
        }
        return "redirect:/trt/listActivePatients";
    }

    @GetMapping("/pay")
    public String patientPay(@RequestParam(value = "patientId", required = true) Long id, @RequestParam(value = "payment", required = true) Integer payment) {
        TreatmentPatient patient = treatmentPatientService.findTreatmentPatientById(id);
        if (patient != null && payment != null) {
            patient.setPaid(patient.getPaid() + payment);
            treatmentPatientService.update(patient);
            System.out.println(patient.getPatientName() + "ID is " + id + " Payment is " + payment);
        }
        return "redirect:/trt/listActivePatients";
    }

    @RequestMapping("/unmarkToday")
    public String unmarkVisit(@ModelAttribute("patientId") Long id) {
        TreatmentPatient patient = treatmentPatientService.findTreatmentPatientById(id);
        if (patient != null) {
            patient.setLastVisit(null);
            TreatmentVisit lastVisit = patient.findLastVisit();
            patient.deleteVisit(lastVisit);
            if (lastVisit != null)
                treatmentVisitService.delete(lastVisit.getId());
            treatmentPatientService.update(patient);
        }
        return "redirect:/trt/listActivePatients";
    }

    @PostMapping("/savePatient")
    public String AddConsultationDoctor(@ModelAttribute("thePatient") TreatmentPatient thePatient) {
        if (thePatient != null) {
            thePatient.setActive(true);
            treatmentPatientService.save(thePatient);
        }
        return "redirect:/trt/listActivePatients";
    }

    @GetMapping("/listAllPatients")
    public String getAllPatients(Model theModel) {
        theModel.addAttribute("theListOfPatients", treatmentPatientService.GetAllPatients());
        theModel.addAttribute("thePatient", new TreatmentPatient());
        return "list-all-treatment-patients";
    }

    @GetMapping("/showFormForEditPatient")
    public String showTreatmentPatientEditForm(@RequestParam("patientId") long patientId, Model theModel) {
        theModel.addAttribute("thePatient", treatmentPatientService.findTreatmentPatientById(patientId));
        theModel.addAttribute("theTreatmentList", treatmentService.getProductList());
        theModel.addAttribute("theTreatmentList", treatmentService.getProductList());
        return "edit-treatment-patient";
    }

    @GetMapping("/deletePatient")
    public String deletePatient(@RequestParam("patientId") long id) {
        treatmentPatientService.delete(id);
        return "redirect:/trt/listAllPatients";
    }

    @PostMapping("/updatePatient")
    public String updateTreatmentPatient(@ModelAttribute("thePatient") TreatmentPatient thePatient) {
        if (thePatient != null) {
            treatmentPatientService.save(thePatient);
        }
        return "redirect:/trt/listActivePatients";
    }

    @GetMapping("/listTreatments")
    public String getTreatmentList(Model theModel) {
        theModel.addAttribute("theListOfTreatments", treatmentService.getProductList());
        theModel.addAttribute("theTreatment", new Treatment());
        return "list-treatments";
    }

    @GetMapping("/deleteTreatment")
    public String deleteTreatment(@RequestParam("id") long id) {
        treatmentService.delete(id);
        return "redirect:/trt/listTreatments";
    }

    @GetMapping("/showFormForEditTreatment")
    public String editTreatment(@RequestParam("id") long id, Model theModel) {
        theModel.addAttribute("theTreatment", treatmentService.findTreatment(id));
        return "edit-treatment";
    }

    @PostMapping("/saveTreatment")
    String saveTreatment(@ModelAttribute("theTreatment") Treatment theTreatment) {
        treatmentService.save(theTreatment);
        return "redirect:/trt/listTreatments";
    }
}
