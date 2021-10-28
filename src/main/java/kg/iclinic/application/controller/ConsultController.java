package kg.iclinic.application.controller;

import kg.iclinic.application.dao.DoctorConsRepository;
import kg.iclinic.application.entity.DoctorCons;
import kg.iclinic.application.entity.Product;
import kg.iclinic.application.service.DoctorConsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Controller
@RequestMapping("/cons")
public class ConsultController {
    @Autowired
    DoctorConsService doctorConsService;

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
}
