package kg.iclinic.application.service;

import kg.iclinic.application.entity.Product;

import java.text.ParseException;
import java.util.List;

public interface ProductService {
    public Product findProduct(Long code);

    void save(Product product) throws ParseException;

    public List<Product> findProductList();

    List<Product> getProductList(List<String> codes);
}
