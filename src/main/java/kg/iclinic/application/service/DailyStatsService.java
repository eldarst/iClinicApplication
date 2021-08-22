package kg.iclinic.application.service;

import javafx.util.Pair;
import kg.iclinic.application.entity.DailyStats;
import kg.iclinic.application.entity.Order;
import kg.iclinic.application.model.StatsPeriod;

import java.text.ParseException;
import java.util.*;

public interface DailyStatsService {
    DailyStats getStatistics(List<Order> orders, Date dateFrom, Date dateTo);

    DailyStats getStatsByDate(Date dateFrom, Date dateTo) throws ParseException;

    void save(DailyStats stats);

    LinkedHashMap<Pair<String,DailyStats>, LinkedHashMap<String, DailyStats>> getPeriodStats(Date lastDayOfPeriod, StatsPeriod periodFunctions);
}
