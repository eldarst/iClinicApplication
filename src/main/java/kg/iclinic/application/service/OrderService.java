package kg.iclinic.application.service;

import kg.iclinic.application.entity.Order;
import kg.iclinic.application.entity.Product;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface OrderService {
    void saveOrder(Order theOrder);
    Order findOrder(Long orderId);
    List<Order> getTodayOrders() throws ParseException;
    List<Order> getOrdersByDay(Date date);
    void deleteOrder(Long orderId);
    List<Order> getSortedOrders(Date dateForm, Date dateTo, String name);
    double countSalary(List<Order> ordersOfDoctor);
}
