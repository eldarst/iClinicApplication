package kg.iclinic.application.service;

import kg.iclinic.application.dao.DailyStatsRepository;
import kg.iclinic.application.entity.DailyStats;
import kg.iclinic.application.entity.Order;
import kg.iclinic.application.model.CountUziStats;
import kg.iclinic.application.model.UziStatistics;
import kg.iclinic.application.model.StatsPeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

@Service
public class DailyStatsServiceImpl implements DailyStatsService{

    DailyStatsRepository dailyStatsRepository;

    @Autowired
    public DailyStatsServiceImpl(DailyStatsRepository dailyStatsRepository) {
        this.dailyStatsRepository = dailyStatsRepository;
    }

    @Autowired
    OrderService orderService;

    Function<LocalDate, Date> parseLocal = java.sql.Date::valueOf;

    @Override
    public DailyStats GetStatistics(List<Order> orders, Date dateFrom, Date dateTo) {
        DailyStats stats = CountUziStats.GetStatsByDate(orders, dateFrom, dateTo);
        return stats;
    }

    @Override
    public DailyStats getStatsByDate(Date dateFrom, Date dateTo) {
        DailyStats stats = dailyStatsRepository.findByDateFromAndDateTo(dateFrom, dateTo);
        Date today = new Date();
        if(dateTo.before(dateFrom)) return new DailyStats();
        if( stats != null && stats.getTotalSum() == 0) {
            dailyStatsRepository.delete(stats);
            DailyStats statistics = getStats(dateFrom, dateTo);
            return statistics;
        } else if (stats != null && stats.getTotalSum() > 0) {
            return stats;
        } else if(stats == null && today.after(dateFrom)){
            DailyStats statistics = getStats(dateFrom, dateTo);
            return statistics;
        }
        return new DailyStats();
    }

    private DailyStats getStats(Date dateFrom, Date dateTo) {
        List<Order> orders = orderService.getSortedOrders(dateFrom, dateTo, "");
        DailyStats statistics = GetStatistics(orders, dateFrom, dateTo);
        dailyStatsRepository.save(statistics);
        return statistics;
    }

    @Override
    public ArrayList<UziStatistics> getPeriodStats(Date lastDayOfPeriod, StatsPeriod periodFunctions) {
        ArrayList<UziStatistics> stats = new ArrayList<>();
        LocalDate lastDay = LocalDate.parse( new SimpleDateFormat("yyyy-MM-dd").format(lastDayOfPeriod) );
        FillStats(stats, lastDay, periodFunctions);
        return stats;
    }

    private void FillStats(ArrayList<UziStatistics> stats, LocalDate lastDay, StatsPeriod periodFunctions) {
        Refresh();
        LocalDate firstDayOfPeriod = periodFunctions.getFirstDayOfAllPeriod().apply(lastDay);

        for(LocalDate day = firstDayOfPeriod; day.isBefore(lastDay);)
            day = FillPeriodStats(stats, lastDay, periodFunctions, day);
    }

    private LocalDate FillPeriodStats(ArrayList<UziStatistics> stats, LocalDate lastDay, StatsPeriod periodFunctions, LocalDate day) {
        int periodCount = periodFunctions.getPeriodCount().apply(day) - 1;
        String periodName = periodFunctions.getPeriodNames().get(periodCount);
        LocalDate periodEnd = periodFunctions.getEndOfSubPeriod().apply(day);
        if(periodEnd.isBefore(lastDay)) {
            LinkedHashMap<String, DailyStats> detailStats = (LinkedHashMap<String, DailyStats>) GetPeriodStats(day, periodEnd, periodFunctions);
            stats.add(new UziStatistics(periodName, getStatsByDate(parseLocal.apply(day), parseLocal.apply(periodEnd)), detailStats));
            day = periodEnd.plusDays(1);
        } else {
            LinkedHashMap<String, DailyStats> detailStats = (LinkedHashMap<String, DailyStats>) GetPeriodStats(day, lastDay, periodFunctions);
            stats.add(new UziStatistics(periodName, getStatsByDate(parseLocal.apply(day), parseLocal.apply(lastDay)), detailStats));
            day = lastDay;
        }
        return day;
    }

    private void Refresh() {
        List<DailyStats> topStats = dailyStatsRepository.findFirst100ByOrderByDateTo();
        topStats.forEach(stat -> dailyStatsRepository.delete(stat));
    }

    private Map<String, DailyStats> GetPeriodStats(LocalDate firstDay, LocalDate periodEnd, StatsPeriod periodFunctions) {
        Map<String, DailyStats> periodStats = new LinkedHashMap<>();
        LocalDate last = periodFunctions.getSubPeriodIncrementing().apply(periodEnd);
        for(LocalDate day = firstDay; day.isBefore(last); day = periodFunctions.getSubPeriodIncrementing().apply(day)) {
            FillSubPeriodStats(periodFunctions, periodStats, day);
        }
        return periodStats;
    }

    private void FillSubPeriodStats(StatsPeriod periodFunctions, Map<String, DailyStats> periodStats, LocalDate day) {
        Date dateFrom = parseLocal.apply(day);
        Date dateTo = parseLocal.apply(day.plusDays(periodFunctions.getSubPeriodLastDay().apply(day)));
        int subPeriodCount = periodFunctions.getSubPeriodCount().apply(day) - 1;
        periodStats.put(periodFunctions.getSubPeriodNames().get(subPeriodCount),
                getStatsByDate(dateFrom, dateTo));
    }

    @Override
    public void save(DailyStats stats) {
        dailyStatsRepository.save(stats);
    }
}
