package kg.iclinic.application.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Orders")
@Data
@NoArgsConstructor
public class Order implements Serializable {

    private static final long serialVersionUID = -2576670215015463100L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "Order_Date", nullable = false)
    @NonNull private Date orderDate;

    @Column(name = "Patient_Name", nullable = false)
    @NonNull private String patientName;

    @Column(name = "Doctor_Name", nullable = false)
    @NonNull private String doctorName;

    @Column(name = "Sum", nullable = false)
    @NonNull private double sum;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "ORDER_DETAILS",
    joinColumns = @JoinColumn(name = "ORDER_ID"),
    inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID"))
    private List<Product> productList;

    public String showProductList() {
        StringBuilder result = new StringBuilder();
        boolean firstElement = true;
        for(Product product: productList){
            if(firstElement) {
                result.append(product.getName());
                firstElement = false;
            } else {
                result.append(", ").append(product.getName());
            }
        }
        return result.toString();
    }

    public void calculateSum() {

        double currentSum = 0;
        for(Product product: productList) {
            currentSum += product.getPrice();
        }
        this.sum = currentSum;
    }
}
