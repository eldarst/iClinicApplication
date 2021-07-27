package kg.iclinic.application.controller;

import kg.iclinic.application.entity.Order;
import kg.iclinic.application.entity.Product;
import kg.iclinic.application.service.AccountService;
import kg.iclinic.application.service.OrderService;
import kg.iclinic.application.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@Controller
@RequestMapping("/uzi")
public class MainController {

    @Autowired
    AccountService accountService;

    @Autowired
    OrderService orderService;

    @Autowired
    ProductService productService;

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

    @GetMapping("/showFormForAddPatient")
    public String showPatientAddForm(Model theModel) {

        Order theOrder = new Order();

        theModel.addAttribute("thePatient", theOrder);
        theModel.addAttribute("theListOfProduct", productService.findProductList());

        return "add-patient";
    }

    @PostMapping("/addPatient")
    public String AddPatient(@ModelAttribute("thePatient") Order thePatient){
                             //@RequestParam List<String> productCodes) {
        if(thePatient != null) {
            thePatient.calculateSum();
            orderService.saveOrder(thePatient);
        }
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
        if(theProduct != null) {
            theProduct.setFrequency(0);
            productService.save(theProduct);
        }
        return "redirect:/uzi/listTodayOrders";
    }

}
