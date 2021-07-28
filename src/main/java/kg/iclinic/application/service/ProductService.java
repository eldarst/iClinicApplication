package kg.iclinic.application.service;

import kg.iclinic.application.entity.Product;

import java.text.ParseException;
import java.util.List;

public interface ProductService {
    Product findProduct(Long code);

    void save(Product product) throws ParseException;

    List<Product> findProductList();

    List<Product> getProductList(List<String> codes);

    void delete(Long productCode);
}
