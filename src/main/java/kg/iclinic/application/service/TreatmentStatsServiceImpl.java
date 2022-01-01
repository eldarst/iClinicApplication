package kg.iclinic.application.service;

import javafx.util.Pair;
import kg.iclinic.application.dao.TreatmentStatsRepository;
import kg.iclinic.application.entity.Treatment;
import kg.iclinic.application.entity.TreatmentStats;
import kg.iclinic.application.entity.TreatmentVisit;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TreatmentStatsServiceImpl implements TreatmentStatsService{

    private final TreatmentStatsRepository treatmentStatsRepository;

    private final TreatmentVisitService treatmentVisitService;

    private final TreatmentService treatmentService;

    public TreatmentStatsServiceImpl(TreatmentStatsRepository treatmentStatsRepository, TreatmentVisitService treatmentVisitService, TreatmentService treatmentService) {
        this.treatmentStatsRepository = treatmentStatsRepository;
        this.treatmentVisitService = treatmentVisitService;
        this.treatmentService = treatmentService;
    }

    @Override
    public TreatmentStats getStatistics(List<TreatmentVisit> visits, Date dateFrom, Date dateTo) {
        return null;
    }

    @Override
    public Set<TreatmentStats> getStatsByDate(Date dateFrom, Date dateTo) {
        Set<TreatmentStats> stats = treatmentStatsRepository.findByDateFromAndDateTo(dateFrom, dateTo);
        int month = dateFrom.getMonth() - new Date().getMonth();
        if (stats == null) return CountStats(dateFrom, dateTo);
        else if (month == 0) {
            treatmentStatsRepository.deleteAll(stats);
            return CountStats(dateFrom, dateTo);
        }
        else return stats;
    }

    private Set<TreatmentStats> CountStats(Date dateFrom, Date dateTo) {
        Set<TreatmentVisit> treatmentVisits = treatmentVisitService.getVisitsByDates(dateFrom, dateTo);
        List<Treatment> treatmentList = treatmentService.getProductList();
        List<Pair<Treatment, List<TreatmentVisit>>> result = treatmentList.stream()
                .map(treatment ->
                        new Pair<>(treatment, treatmentVisits.stream()
                                .filter(visit -> visit.getPatient().getTreatment().equals(treatment))
                                .collect(Collectors.toList()))
                )
                .collect(Collectors.toList());
        return result.stream()
                .map(s -> {
                    TreatmentStats stat = new TreatmentStats();
                    stat.setTreatment(s.getKey());
                    stat.setDateFrom(dateFrom);
                    stat.setDateTo(dateTo);
                    stat.setVisitsCount(s.getValue().size());
                    stat.setPrice((int) s.getKey().getPriceForOneVisit());
                    int patientCount = s.getValue().stream()
                            .collect(Collectors.groupingBy(TreatmentVisit::getPatient))
                            .size();
                    stat.setPatientCount(patientCount);
                    stat.setSum(s.getValue().size() * (int) s.getKey().getPriceForOneVisit());
                    return stat;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public void save(TreatmentStats stats) {
        treatmentStatsRepository.save(stats);
    }
}
