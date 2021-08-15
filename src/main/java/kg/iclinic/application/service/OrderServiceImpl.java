package kg.iclinic.application.service;

import kg.iclinic.application.dao.OrderRepository;
import kg.iclinic.application.entity.Order;
import kg.iclinic.application.model.Methods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void saveOrder(Order theOrder) {
        theOrder.setOrderDate(Methods.getTodaysDate());
        orderRepository.save(theOrder);
    }

    @Override
    public Order findOrder(Long orderId) {
        return orderRepository.getById(orderId);
    }


    @Override
    public List<Order> getTodayOrders() {
        Date today = Methods.getTodaysDate();
        return orderRepository.findByOrderDate(today);
    }

    @Override
    public List<Order> getOrdersByDay(Date date) {
        return orderRepository.findByOrderDate(date);
    }

    @Override
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.getById(orderId);
        orderRepository.delete(order);
    }

    @Override
    public List<Order> getSortedOrders(Date dateForm, Date dateTo, String name) {
        return (name.length() > 0)
                ? orderRepository.findByOrderDateBetweenAndDoctorName(dateForm, dateTo, name)
                : orderRepository.findByOrderDateBetween(dateForm, dateTo);
    }

    @Override
    public double countSalary(List<Order> ordersOfDoctor) {
        double result = 0;
        for(Order order: ordersOfDoctor) {
            result += order.getSum();
        }
        return result;
    }


}
