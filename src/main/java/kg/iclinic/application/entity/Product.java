package kg.iclinic.application.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Products")
@SQLDelete(sql = "UPDATE products SET is_deleted = true WHERE code=?")
@Where(clause = "is_deleted=false")
public class Product implements Serializable {
    private static final long serialVersionUID = -1000119078147252957L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Code", length = 20, nullable = false)
    private long code;

    @Column(name = "Name", nullable = false)
    @NonNull private String name;

    @Column(name = "Price", nullable = false)
    @NonNull private Double price;

    @Temporal(TemporalType.DATE)
    @Column(name = "Create_Date", nullable = false)
    @NonNull private Date createDate;

    @Column(name = "Is_Deleted", nullable = false)
    @NonNull private Boolean isDeleted;

    @Column(name = "Frequency")
    @NonNull private Integer frequency = 0;

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
