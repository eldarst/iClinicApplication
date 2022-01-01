package kg.iclinic.application.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Treatment_Stats")
@Getter
@Setter
@NoArgsConstructor
public class TreatmentStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @OneToOne
    @JoinColumn(name = "treatment_id")
    private Treatment treatment;

    @Column(name = "Price", nullable = false)
    private int price;

    @Column(name = "Patient_Count", nullable = false)
    private int patientCount;

    @Column(name = "Visit_Count", nullable = false)
    private int visitsCount;

    @Column(name = "Sum", nullable = false)
    private int sum;

    @Temporal(TemporalType.DATE)
    @Column(name = "Date_From", nullable = false)
    private Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "Date_To", nullable = false)
    private Date dateTo;
}
