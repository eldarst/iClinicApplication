package kg.iclinic.application.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Treatment")
public class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "Treatment_Name", nullable = false)
    @NonNull private String treatmentName;

    @Column(name = "Full_Treatment", nullable = false)
    @NonNull private int fullTreatmentCount;

    @Column(name = "Treatment_Price", nullable = false)
    @NonNull private int treatmentPrice;

    @Override
    public String toString() {
        return String.format("%s, %d раз, %s сом", treatmentName, fullTreatmentCount, treatmentPrice);
    }
}
