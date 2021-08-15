package kg.iclinic.application.service;

import kg.iclinic.application.dao.DailyStatsRepository;
import kg.iclinic.application.entity.DailyStats;
import kg.iclinic.application.entity.Order;
import kg.iclinic.application.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DailyStatsServiceImpl implements DailyStatsService{

    DailyStatsRepository dailyStatsRepository;

    @Autowired
    public DailyStatsServiceImpl(DailyStatsRepository dailyStatsRepository) {
        this.dailyStatsRepository = dailyStatsRepository;
    }

    @Override
    public DailyStats getStatistics(List<Order> orders) {
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
        stats.setMostFrequentDoctorCount(orders.stream().filter(o -> o.getDoctorName().equalsIgnoreCase(stats.getMostFrequentDoctor())).count());
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
        return stats;
    }

    @Autowired
    OrderService orderService;

    @Override
    public DailyStats getStatsByDate(Date date) throws ParseException {
        DailyStats stats = dailyStatsRepository.findByDateFromAndDateTo(date, date);
        Date today = new Date();
        if (stats != null) {
            return stats;
        } else if(stats == null && today.after(date)){
            List<Order> orders = orderService.getOrdersByDay(date);
            DailyStats statistics = getStatistics(orders);
            statistics.setDateFrom(date);
            statistics.setDateTo(date);
            dailyStatsRepository.save(statistics);
            return statistics;
        }
        return new DailyStats();
    }

    @Override
    public void save(DailyStats stats) {
        dailyStatsRepository.save(stats);
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


}
