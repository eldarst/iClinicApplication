package kg.iclinic.application.controller;

import javafx.util.Pair;
import kg.iclinic.application.entity.Doctor;
import kg.iclinic.application.entity.Order;
import kg.iclinic.application.entity.Product;
import kg.iclinic.application.service.AccountService;
import kg.iclinic.application.service.DoctorService;
import kg.iclinic.application.service.OrderService;
import kg.iclinic.application.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

@Controller
@RequestMapping("/uzi")
public class MainController {

    @Autowired
    AccountService accountService;

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

        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy");
        theModel.addAttribute("theDate", formatter.parse(formatter.format(new Date())).toString());
        theModel.addAttribute("theListOfPatients", orderService.getTodayOrders());

        Order theOrder = new Order();

        theModel.addAttribute("thePatient", theOrder);
        theModel.addAttribute("theListOfProduct", productService.findProductList());

        return "list-patients";
    }

    @GetMapping("/showFormForEditPatient")
    public String showPatientEditForm(@RequestParam("orderId") long theId, Model theModel) {

        Order theOrder = orderService.findOrder(theId);

        theModel.addAttribute("thePatient", theOrder);
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

    @GetMapping("/showFormForAddProduct")
    public String showProductAddForm(Model theModel) {
        Product theProduct = new Product();
        theModel.addAttribute("theProduct", theProduct);
        return "add-product";
    }

    @PostMapping("/addProduct")
    public String AddProduct(@ModelAttribute("theProduct") Product theProduct) throws ParseException {
        if (theProduct != null) {
            theProduct.setFrequency(0);
            productService.save(theProduct);
        }
        return "redirect:/uzi/listProducts";
    }

    @GetMapping("/listProducts")
    public String getProductList(Model theModel) {
        theModel.addAttribute("theProduct", new Product());
        theModel.addAttribute("theListOfProduct", productService.findProductList());

        return "list-product";
    }

    @GetMapping("/deleteProduct")
    public String deleteProduct(@RequestParam("productCode") long productCode) {
        productService.delete(productCode);
        return "redirect:/uzi/listProducts";
    }

    @GetMapping("/showFormForEditProduct")
    public String showProductEditForm(@RequestParam("productCode") long productCode, Model theModel) {
        Product product = productService.findProduct(productCode);

        theModel.addAttribute("theProduct", product);
        return "add-product";
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
        DateFormat dateFormat = new SimpleDateFormat(
                "MM/dd/yyyy", Locale.US);
        Date firstDate = new Date();
        Date secondDate = new Date();
        if(dateFrom.length() > 0 && dateTo.length() > 0) {
             firstDate = dateFormat.parse(dateFrom);
             secondDate = dateFormat.parse(dateTo);
        }
        List<Order> sortedOrders = orderService.getSortedOrders(firstDate, secondDate, doctor);

        theModel.addAttribute("theListOfPatients", sortedOrders);
        theModel.addAttribute("theListOfDoctor", doctorService.findListOfDoctors());
        theModel.addAttribute("dateFrom", firstDate);
        theModel.addAttribute("dateTo", secondDate);
        theModel.addAttribute("doctorName", doctor);
        return "sort-by-date";
    }

    @GetMapping("/showSalariesBetweenDatesForm")
    public String showSalariesBetweenDatesForm(Model theModel) {
        theModel.addAttribute("dateFrom", new Date());
        theModel.addAttribute("dateTo", new Date());
        return "salary-by-date";
    }

    @RequestMapping("/listSalariesBetweenDates")
    public String getSalariesBetweenDates(@RequestParam(value = "dateFrom", required = false) String dateFrom,
                                          @RequestParam(value = "dateTo", required = false) String dateTo,
                                          Model theModel) throws ParseException{
        DateFormat dateFormat = new SimpleDateFormat(
                "MM/dd/yyyy", Locale.US);
        Date firstDate = new Date();
        Date secondDate = new Date();
        if(dateFrom.length() > 0 && dateTo.length() > 0) {
            firstDate = dateFormat.parse(dateFrom);
            secondDate = dateFormat.parse(dateTo);
        }
        HashMap<String, Pair<Double, Integer>> doctorsSalaries = GetMappedSalary(firstDate, secondDate);

        theModel.addAttribute("theListOfDoctorsWithSalaries", doctorsSalaries);
        theModel.addAttribute("dateFrom", firstDate);
        theModel.addAttribute("dateTo", secondDate);
        return "salary-by-date";
    }

    @GetMapping("/listCurrentWeekSalary")
    public String getCurrentWeekSalaries(Model theModel) {
        Date firstDayOfWeek = GetFirstDayOfTheWeek();
        System.out.println(firstDayOfWeek);
        HashMap<String, Pair<Double, Integer>> doctorsSalaries = GetMappedSalary(firstDayOfWeek, new Date());
        theModel.addAttribute("theListOfDoctorsWithSalaries", doctorsSalaries);
        return "current-week-salary";
    }

    private Date GetFirstDayOfTheWeek() {
        LocalDate today = LocalDate.now();

        LocalDate monday = today.with(previousOrSame(MONDAY));
        //LocalDate sunday = today.with(nextOrSame(SUNDAY));
        return java.sql.Date.valueOf(monday);
    }

    private HashMap<String, Pair<Double, Integer>> GetMappedSalary(Date dateFrom, Date dateTo) {
        List<Doctor> doctors = doctorService.findListOfDoctors();
        HashMap<String, Pair<Double, Integer>> result = new HashMap<>();
        for(Doctor doctor: doctors) {
            List<Order> sortedOrders = orderService.getSortedOrders(dateFrom, dateTo, doctor.getName());
            if(sortedOrders.size() > 0)
                result.put(doctor.getName(),
                        new Pair<>((double) Math.round(orderService.countSalary(sortedOrders) * 0.2), sortedOrders.size()));
        }
        return result;
    }


}
