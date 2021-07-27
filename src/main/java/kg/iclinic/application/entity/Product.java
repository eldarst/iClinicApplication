package kg.iclinic.application.entity;

import kg.iclinic.application.model.Methods;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Products")
@Data
public class Product implements Serializable {

    private static final long serialVersionUID = -1000119078147252957L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Code", length = 20, nullable = false)
    private long code;

    @Column(name = "Name", length = 255, nullable = false)
    private String name;

    @Column(name = "Price", nullable = false)
    private Double price;

    @Temporal(TemporalType.DATE)
    @Column(name = "Create_Date", nullable = false)
    private Date createDate;

    @Column(name = "Frequency")
    private Integer frequency = 0;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "ORDER_DETAILS",
            joinColumns = @JoinColumn(name = "PRODUCT_ID"),
            inverseJoinColumns = @JoinColumn(name = "ORDER_ID"))
    private List<Order> ordersList;
    @Override
    public String toString() {
        return name + " ," + price + "с";
    }

    public void updateFrequency(){
        this.frequency++;
    }
}
