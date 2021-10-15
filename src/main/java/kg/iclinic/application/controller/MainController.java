package kg.iclinic.application.controller;

import kg.iclinic.application.entity.Order;
import kg.iclinic.application.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
@RequestMapping("/uzi")
public class MainController {
    private static final DateFormat dateFormat = new SimpleDateFormat(
            "MM/dd/yyyy", Locale.US);

    @Autowired
    OrderService orderService;

    @Autowired
    ProductService productService;

    @Autowired
    DoctorService doctorService;


    @RequestMapping("/403")
    public String accessDenied() {
        return "/403";
    }

    @GetMapping("/")
    public String home() {
        return "list-patients";
    }


    @GetMapping("/listTodayOrders")
    public String getTodayOrders(Model theModel) throws ParseException {
        theModel.addAttribute("theDate", dateFormat.parse(dateFormat.format(new Date())).toString());
        List<Order> todayOrders = orderService.getTodayOrders();
        theModel.addAttribute("theListOfPatients", todayOrders);
        theModel.addAttribute("orderSum", orderService.countSalary(todayOrders));
        theModel.addAttribute("thePatient", new Order());
        theModel.addAttribute("theListOfProduct", productService.findProductList());
        theModel.addAttribute("theListOfDoctor", doctorService.findListOfDoctors());

        return "list-patients";
    }

    @GetMapping("/showFormForEditPatient")
    public String showPatientEditForm(@RequestParam("orderId") long theId, Model theModel) {
        theModel.addAttribute("thePatient", orderService.findOrder(theId));
        theModel.addAttribute("theListOfProduct", productService.findProductList());

        return "edit-patient";
    }

    @PostMapping("/savePatient")
    public String AddPatient(@ModelAttribute("thePatient") Order thePatient) {
        if (thePatient != null) {
            thePatient.calculateSum();
            String doctorName = thePatient.getDoctorName();
            doctorService.save(doctorName);
            orderService.saveOrder(thePatient);
        }
        return "redirect:/uzi/listTodayOrders";
    }

    @GetMapping("/deleteOrder")
    public String deletePatient(@RequestParam("orderId") long orderId) {
        orderService.deleteOrder(orderId);
        return "redirect:/uzi/listTodayOrders";
    }




    @GetMapping("/showSortByDateForm")
    public String showSortByDateForm(Model theModel) {
        theModel.addAttribute("dateFrom", new Date());
        theModel.addAttribute("dateTo", new Date());
        theModel.addAttribute("doctorName", "");
        theModel.addAttribute("theListOfDoctor", doctorService.findListOfDoctors());
        theModel.addAttribute("theListOfPatients", new ArrayList<Order>());
        return "sort-by-date";
    }

    @RequestMapping("/listOrderBetweenDates")
    public String getOrdersBetweenDates(@RequestParam(value = "dateFrom", required = false) String dateFrom,
                                        @RequestParam(value = "dateTo", required = false) String dateTo,
                                        @RequestParam(value = "doctorName", required = false) String doctor,
                                        Model theModel) throws ParseException {
        Date firstDate = dateFrom!= null ? dateFormat.parse(dateFrom) : new Date();
        Date secondDate = dateTo != null ? dateFormat.parse(dateTo): new Date();
        List<Order> sortedOrders = orderService.getSortedOrders(firstDate, secondDate, (doctor != null) ? doctor : "" );

        theModel.addAttribute("theListOfDoctor", doctorService.findListOfDoctors());
        theModel.addAttribute("theListOfPatients", sortedOrders);
        theModel.addAttribute("theListOfDoctor", doctorService.findListOfDoctors());
        theModel.addAttribute("dateFrom", firstDate);
        theModel.addAttribute("dateTo", secondDate);
        theModel.addAttribute("doctorName", doctor);
        return "sort-by-date";
    }


}
