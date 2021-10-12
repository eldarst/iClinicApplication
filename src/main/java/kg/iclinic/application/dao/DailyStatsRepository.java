package kg.iclinic.application.dao;

import kg.iclinic.application.entity.DailyStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DailyStatsRepository extends JpaRepository<DailyStats, Long> {
    DailyStats findByDateFromAndDateTo(Date dateFrom, Date dateTo);
    List<DailyStats> findFirst100ByOrderByDateTo();
}
