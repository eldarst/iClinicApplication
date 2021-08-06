package kg.iclinic.application.service;

import kg.iclinic.application.entity.Order;
import kg.iclinic.application.entity.Product;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface OrderService {
    public void saveOrder(Order theOrder);
    public Order findOrder(Long orderId);
    public List<Product> listProducts(Long orderId);
    public List<Order> getTodayOrders() throws ParseException;
    public List<Order> getAllOrders();
    public void deleteOrder(Long orderId);
    List<Order> getSortedOrders(Date dateForm, Date dateTo, String name);
    double countSalary(List<Order> ordersOfDoctor);
}
