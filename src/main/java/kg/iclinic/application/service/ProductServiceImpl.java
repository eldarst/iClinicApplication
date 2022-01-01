package kg.iclinic.application.service;

import kg.iclinic.application.dao.ProductRepository;
import kg.iclinic.application.entity.Product;
import kg.iclinic.application.model.Methods;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    private ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product findProduct(Long code) {
        Product product = productRepository.getById(code);
        product.updateFrequency();
        return product;
    }

    @Override
    public void save(Product product) {
        Date today = Methods.getTodaysDate();
        product.setCreateDate(today);
        product.setIsDeleted(false);
        productRepository.save(product);
    }


    @Override
    public void delete(Long productCode) {
        Product product = productRepository.getById(productCode);
        productRepository.delete(product);
    }


    @Override
    public List<Product> findProductList() {
        return productRepository.findByOrderByFrequencyDesc();
    }
}
