package kg.iclinic.application.dao;

import kg.iclinic.application.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    public List<Order> findByOrderDate(Date now);

    List<Order> findByOrderDateBetweenAndDoctorName(Date start, Date end, String doctorName);
    List<Order> findByOrderDateBetween(Date start, Date end);
}
