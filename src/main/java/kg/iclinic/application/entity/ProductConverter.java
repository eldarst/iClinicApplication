package kg.iclinic.application.entity;

import kg.iclinic.application.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class ProductConverter implements Converter<String, Product> {
    @Autowired
    ProductService productService;
    @Override
    public Product convert(String code) {
        return productService.findProduct(Long.parseLong(code));
    }
}
