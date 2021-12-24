package kg.iclinic.application.controller;

import kg.iclinic.application.entity.Product;
import kg.iclinic.application.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Controller
@RequestMapping("/uzi")
public class ProductUziController {

    @Autowired
    ProductService productService;

    @GetMapping("/showFormForAddProduct")
    public String showProductAddForm(Model theModel) {
        Product theProduct = new Product();
        theModel.addAttribute("theProduct", theProduct);
        return "edit-product";
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
        theModel.addAttribute("theProduct", productService.findProduct(productCode));
        return "edit-product";
    }

}
