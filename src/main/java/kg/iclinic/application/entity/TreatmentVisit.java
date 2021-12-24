package kg.iclinic.application.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Visit_TR")
@Getter
@Setter
@NoArgsConstructor
public class TreatmentVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "Visit_Date", nullable = false)
    private Date visitDate;

    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "PATIENT_VISIT_TR",
            joinColumns = @JoinColumn(name = "VISIT_TR_ID"),
            inverseJoinColumns = @JoinColumn(name = "PATIENT_TR_ID"))
    private TreatmentPatient patient;

    public TreatmentVisit(TreatmentPatient patient, Date visitDate) {
        this.patient = patient;
        this.visitDate = visitDate;
    }
}
