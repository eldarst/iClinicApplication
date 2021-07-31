package kg.iclinic.application.controller;

import kg.iclinic.application.entity.Order;
import kg.iclinic.application.entity.Product;
import kg.iclinic.application.service.AccountService;
import kg.iclinic.application.service.DoctorService;
import kg.iclinic.application.service.OrderService;
import kg.iclinic.application.service.ProductService;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        theModel.addAttribute("doctorName", new String());
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
        Date firstDate = dateFormat.parse(dateFrom);
        Date secondDate = dateFormat.parse(dateTo);
        List<Order> sortedOrders = orderService.getSortedOrders(firstDate, secondDate, doctor);

        System.out.println(doctor);
        System.out.println(doctorService.findListOfDoctors().size() + " " + doctorService.findListOfDoctors());
        theModel.addAttribute("theListOfDoctor", doctorService.findListOfDoctors());
        theModel.addAttribute("theListOfPatients", sortedOrders);
        theModel.addAttribute("dateFrom", firstDate);
        theModel.addAttribute("dateTo", secondDate);
        theModel.addAttribute("doctorName", doctor);

        theModel.addAttribute("thePatient", new Order());
        theModel.addAttribute("theListOfProduct", productService.findProductList());
        return "sort-by-date";
    }

}
