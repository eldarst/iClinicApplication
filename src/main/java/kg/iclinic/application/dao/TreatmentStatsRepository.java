package kg.iclinic.application.dao;

import kg.iclinic.application.entity.TreatmentStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface TreatmentStatsRepository extends JpaRepository<TreatmentStats, Long> {
    Set<TreatmentStats> findByDateFromAndDateTo(Date dateFrom, Date dateTo);
    List<TreatmentStats> findFirst100ByOrderByDateTo();
}
