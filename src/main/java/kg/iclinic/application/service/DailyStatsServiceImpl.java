package kg.iclinic.application.service;

import javafx.util.Pair;
import kg.iclinic.application.dao.DailyStatsRepository;
import kg.iclinic.application.entity.DailyStats;
import kg.iclinic.application.entity.Order;
import kg.iclinic.application.entity.Product;
import kg.iclinic.application.model.Methods;
import kg.iclinic.application.model.StatsPeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public DailyStats getStatistics(List<Order> orders, Date dateFrom, Date dateTo) {
        DailyStats stats = new DailyStats();
        stats.setMaxOrder(orders.stream()
                .mapToDouble(Order::getSum).max()
                .orElse(0.));

        stats.setAverageOrder((int) orders.stream()
                .mapToDouble(Order::getSum)
                .average().orElse(0.));

        stats.setMostFrequentDoctor(orders.stream()
                .map(Order::getDoctorName).filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(""));

        stats.setMostFrequentDoctorCount(orders.stream().filter(o -> o.getDoctorName()
                .equalsIgnoreCase(stats.getMostFrequentDoctor())).count());

        stats.setMostFrequentProduct(orders.stream()
                .flatMap(v -> v.getProductList().stream())
                .collect(Collectors.toList()).stream()
                .map(Product::getName).filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(""));

        stats.setMostFrequentProductCount(orders.stream()
                .flatMap(v -> v.getProductList().stream())
                .collect(Collectors.toList()).stream()
                .filter(o -> o.getName().equalsIgnoreCase(stats.getMostFrequentProduct())).count());

        CountStats(stats, orders);

        stats.setDateTo(dateTo);
        stats.setDateFrom(dateFrom);

        return stats;
    }



    private void CountStats(DailyStats stats, List<Order> orders) {
        int uziSum = 0, patientCount = 0, patientsUnknownDocCount = 0;
        double totalSum = 0., unknownPatientsSum = 0.;
        for(Order order: orders) {
            double sum = order.getSum();
            totalSum += sum;
            ++patientCount;
            if(order.getDoctorName().equalsIgnoreCase("неизв")) {
                uziSum += (int) sum * 0.5;
                ++patientsUnknownDocCount;
                unknownPatientsSum += sum;
            } else {
                uziSum += (int) sum * 0.4;
            }
        }
        stats.setTotalSum(totalSum);
        stats.setDoctorTotalSum((int)totalSum - 2 * uziSum);
        stats.setUziTotalSum(uziSum);
        stats.setUnknownPatientsTotalSum(unknownPatientsSum);
        stats.setTotalPatients(patientCount);
        stats.setUnknownPatients(patientsUnknownDocCount);
    }

    @Override
    public DailyStats getStatsByDate(Date dateFrom, Date dateTo) {
        DailyStats stats = dailyStatsRepository.findByDateFromAndDateTo(dateFrom, dateTo);
        Date today = new Date();
        if (stats != null) {
            return stats;
        } else if(stats == null && today.after(dateFrom)){
            List<Order> orders = orderService.getSortedOrders(dateFrom, dateTo,"");
            DailyStats statistics = getStatistics(orders, dateFrom, dateTo);
            dailyStatsRepository.save(statistics);
            return statistics;
        }
        return new DailyStats();
    }

    @Override
    public Map<DailyStats, Map<String, DailyStats>> getPeriodStats(Date lastDayOfPeriod, StatsPeriod periodFunctions) {
        Map<DailyStats, Map<String, DailyStats>> stats = new HashMap<>();
        LocalDate lastDay = LocalDate.parse( new SimpleDateFormat("yyyy-MM-dd").format(lastDayOfPeriod) );
        FillStats(stats, lastDay, periodFunctions);
        return stats;
    }

    private void FillStats(Map<DailyStats, Map<String, DailyStats>> stats, LocalDate lastDay, StatsPeriod periodFunctions) {
        LocalDate firstDayOfPeriod = periodFunctions.getFirstDayOfAllPeriod().apply(lastDay);

        int i = 0;
        for(LocalDate day = firstDayOfPeriod; day.isBefore(lastDay);) {
            String subPeriodName = periodFunctions.getSubPeriodNames().get(i++ % periodFunctions.getSubPeriodNames().size());
            LocalDate periodEnd = periodFunctions.getEndOfSubPeriod().apply(day);
            if(periodEnd.isBefore(lastDay)) {
                Map<String, DailyStats> detailStats = GetPeriodStats(day, periodEnd, periodFunctions);
                stats.put(getStatsByDate(parseLocal.apply(day), parseLocal.apply(periodEnd)),
                        detailStats);
                day = periodEnd.plusDays(1);
            } else {
                Map<String, DailyStats> detailStats = GetPeriodStats(day, lastDay, periodFunctions);
                stats.put(getStatsByDate(parseLocal.apply(day), parseLocal.apply(lastDay)),
                        detailStats);
                day = lastDay;
            }
        }
    }

    private Map<String, DailyStats> GetPeriodStats(LocalDate firstDay, LocalDate periodEnd, StatsPeriod periodFunctions) {
        Map<String, DailyStats> periodStats = new HashMap<>();
        for(LocalDate day = firstDay; day.isBefore(periodEnd); day = periodFunctions.getSubPeriodIncrementing().apply(day)) {
            Date dateFrom = parseLocal.apply(day);
            Date dateTo = parseLocal.apply(day.plusDays(periodFunctions.getPeriod()));
            periodStats.put(periodFunctions.getSubPeriodNames()
                            .get(periodFunctions.getSubPeriodName().apply(day) - 1),
                    getStatsByDate(dateFrom, dateTo));
        }
        return periodStats;
    }

    @Override
    public void save(DailyStats stats) {
        dailyStatsRepository.save(stats);
    }


}