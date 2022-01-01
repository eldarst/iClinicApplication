package kg.iclinic.application.entity;

import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "patient_tr")
@Getter
@Setter
@NoArgsConstructor
public class TreatmentPatient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "Patient_Name", nullable = false)
    @NonNull
    private String patientName;

    @Column(name = "Is_Active", nullable = false)
    private boolean isActive;

    @Temporal(TemporalType.DATE)
    @Column(name = "Last_Visit")
    private Date lastVisit;

    @Column(name = "Paid", nullable = false)
    private int paid;

    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "Treatment_Id", referencedColumnName = "id")
    private Treatment treatment;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "PATIENT_VISIT_TR",
            joinColumns = @JoinColumn(name = "PATIENT_TR_ID"),
            inverseJoinColumns = @JoinColumn(name = "VISIT_TR_ID"))
    private Set<TreatmentVisit> visits;

    public double getForOne() {
        if (treatment != null)
            return treatment.getTreatmentPrice() / treatment.getFullTreatmentCount();
        return 0;
    }
    public int getDebt() {
        if (visits == null) {
            return 0;
        }
        int mustBePaid = visits.size() * (int) getForOne();
        return mustBePaid > paid ? mustBePaid - paid : 0;
    }

    public int getPaymentForToday() {
        int forPay = (visits == null)
                ? (int) getForOne() - paid
                : (int) getForOne() * (visits.size() + 1) - paid;
        return Math.max(forPay, 0);
    }
    public String getVisitsCount() {
        if (visits == null) {
            return String.format("%d / %d", 0, treatment.getFullTreatmentCount());
        }
        return String.format("%d / %d", visits.size(), treatment.getFullTreatmentCount());
    }

    public String getPaidMoney() {
        return String.format("%d сом / %d сом", paid, treatment.getTreatmentPrice());
    }

    public void addVisit(TreatmentVisit visit) {
        if (visits == null) {
            visits = new HashSet<>();
        }
        visits.add(visit);
    }

    public void deleteVisit(TreatmentVisit visit) {
        if (visit != null) {
            visits.remove(visit);
        }
    }

    public TreatmentVisit findLastVisit() {
        TreatmentVisit lastVisit = visits.stream()
                .max(Comparator.comparing(TreatmentVisit::getVisitDate))
                .orElse(null);
        return lastVisit;
    }
}
