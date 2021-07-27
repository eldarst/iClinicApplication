package kg.iclinic.application.dao;

import kg.iclinic.application.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = ("Select max(o.orderNum) from Order o"), nativeQuery = true)
    public Integer findMaxOrderNum();
    public List<Order> findByOrderDate(Date now);
}
