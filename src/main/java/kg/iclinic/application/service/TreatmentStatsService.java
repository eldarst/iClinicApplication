package kg.iclinic.application.service;


import kg.iclinic.application.entity.TreatmentStats;
import kg.iclinic.application.entity.TreatmentVisit;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface TreatmentStatsService {

    TreatmentStats getStatistics(List<TreatmentVisit> visits, Date dateFrom, Date dateTo);

    Set<TreatmentStats> getStatsByDate(Date dateFrom, Date dateTo);

    void save(TreatmentStats stats);

}
