package kg.iclinic.application.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Consultation")
@Data
@RequiredArgsConstructor
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @OneToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "Doctor_Id", referencedColumnName = "id")
    private DoctorCons doctor;

    @Column(name = "Patient", nullable = false)
    private String patient;

    @Temporal(TemporalType.DATE)
    @Column(name = "Consultation_Date", nullable = false)
    private Date consultationDate;

    @Column(name = "Price", nullable = false)
    private int price;

    @Column(name = "For_Clinic", nullable = false)
    private int forClinic;

    @Temporal(TemporalType.DATE)
    @Column(name = "Previous_Visit", nullable = false)
    private Date previousVisit;
}
