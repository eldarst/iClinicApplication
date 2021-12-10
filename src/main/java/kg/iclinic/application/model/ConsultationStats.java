package kg.iclinic.application.model;

import kg.iclinic.application.entity.Consultation;
import kg.iclinic.application.entity.DoctorCons;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationStats {
    private String doctor;
    private int patientCount;
    private int totalSum;
    private int forClinicSum;


    public static List<ConsultationStats> GetConsultationStats(List<Consultation> consultations) {
        Map<DoctorCons, List<Consultation>> doctorCons = consultations.stream()
                .collect(groupingBy(Consultation::getDoctor));
        return CountConsultationStats(doctorCons);
    }

    private static List<ConsultationStats> CountConsultationStats(Map<DoctorCons, List<Consultation>> doctorCons) {
        List<ConsultationStats> consultationStats = new ArrayList<>();
        for (DoctorCons doc : doctorCons.keySet()) {
            ConsultationStats stats = CountDoctorStats(doctorCons, doc);
            consultationStats.add(stats);
        }
        return consultationStats;
    }

    private static ConsultationStats CountDoctorStats(Map<DoctorCons, List<Consultation>> doctorCons, DoctorCons doc) {
        ConsultationStats stats = new ConsultationStats();
        int forClinic = 0, totalSum = 0;
        for (Consultation cons : doctorCons.get(doc)) {
            forClinic += cons.getForClinic();
            totalSum += cons.getPrice();
        }
        stats.setForClinicSum(forClinic);
        stats.setTotalSum(totalSum);
        stats.setPatientCount(doctorCons.get(doc).size());
        stats.setDoctor(doc.getName());
        return stats;
    }

}
