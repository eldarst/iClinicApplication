package kg.iclinic.application.service;

import kg.iclinic.application.entity.DailyStats;
import kg.iclinic.application.entity.Order;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface DailyStatsService {
    DailyStats getStatistics(List<Order> orders);
    DailyStats getStatsByDate(Date date) throws ParseException;
    void save(DailyStats stats);
}
