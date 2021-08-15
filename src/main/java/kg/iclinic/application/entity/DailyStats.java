package kg.iclinic.application.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Daily_Stats")
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class DailyStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "Date_From", nullable = false)
    @NonNull private Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "Date_To", nullable = false)
    @NonNull private Date dateTo;

    @Column(name = "Total_Sum", nullable = false)
    @NonNull private double totalSum;

    @Column(name = "Uzi_Sum")
    @NonNull private int uziTotalSum;

    @Column(name = "Doctor_Sum", nullable = false)
    @NonNull private int doctorTotalSum;

    @Column(name = "Unknown_Patients_Sum", nullable = false)
    @NonNull private double unknownPatientsTotalSum;

    @Column(name = "Patient_Count", nullable = false)
    @NonNull private int totalPatients;

    @Column(name = "Unknown_Patient_Count", nullable = false)
    @NonNull private int unknownPatients;

    @Column(name = "Max_Order", nullable = false)
    @NonNull private double maxOrder;

    @Column(name = "Average_Order", nullable = false)
    @NonNull private double averageOrder;

    @Column(name = "Most_Frequent_Doctor", nullable = false)
    @NonNull private String mostFrequentDoctor;

    @Column(name = "Most_Frequent_Doctor_Count", nullable = false)
    @NonNull private long mostFrequentDoctorCount;

    @Column(name = "Most_Frequent_Product", nullable = false)
    @NonNull private String mostFrequentProduct;

    @Column(name = "Most_Frequent_Product_Count", nullable = false)
    @NonNull private long mostFrequentProductCount;
}
