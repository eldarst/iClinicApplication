package kg.iclinic.application.service;

import javafx.util.Pair;
import kg.iclinic.application.entity.DailyStats;
import kg.iclinic.application.entity.Order;
import kg.iclinic.application.model.Statistics;
import kg.iclinic.application.model.StatsPeriod;

import java.text.ParseException;
import java.util.*;

public interface DailyStatsService {
    DailyStats GetStatistics(List<Order> orders, Date dateFrom, Date dateTo);

    DailyStats getStatsByDate(Date dateFrom, Date dateTo) throws ParseException;

    void save(DailyStats stats);

    List<Statistics> getPeriodStats(Date lastDayOfPeriod, StatsPeriod periodFunctions);
}
