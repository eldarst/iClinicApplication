package kg.iclinic.application.model;

import kg.iclinic.application.entity.DailyStats;
import kg.iclinic.application.entity.Order;
import kg.iclinic.application.entity.Product;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CountStats {

    public static DailyStats GetStatsByDate(List<Order> orders, Date dateFrom, Date dateTo) {
        DailyStats stats = new DailyStats();
        stats.setMaxOrder(GetMaxOrder(orders));

        stats.setAverageOrder((int) GetAverageOrder(orders));

        stats.setMostFrequentDoctor(GetFrequentDoctor(orders));

        stats.setMostFrequentDoctorCount(GetMostFrequentDoctorCount(orders, stats));

        stats.setMostFrequentProduct(GetMostFrequentProduct(orders));

        stats.setMostFrequentProductCount(GetMostFrequentProductCount(orders, stats));

        CountStatistics(stats, orders);

        stats.setDateTo(dateTo);
        stats.setDateFrom(dateFrom);

        return stats;
    }

    private static long GetMostFrequentProductCount(List<Order> orders, DailyStats stats) {
        return orders.stream()
                .flatMap(v -> v.getProductList().stream())
                .collect(Collectors.toList()).stream()
                .filter(o -> o.getName().equalsIgnoreCase(stats.getMostFrequentProduct())).count();
    }

    private static String GetMostFrequentProduct(List<Order> orders) {
        return orders.stream()
                .flatMap(v -> v.getProductList().stream())
                .map(Product::getName).filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
    }

    private static long GetMostFrequentDoctorCount(List<Order> orders, DailyStats stats) {
        return orders.stream()
                .filter(o -> o.getDoctorName().equalsIgnoreCase(stats.getMostFrequentDoctor()))
                .count();
    }

    private static String GetFrequentDoctor(List<Order> orders) {
        return orders.stream()
                .map(Order::getDoctorName).filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse("");
    }

    private static double GetAverageOrder(List<Order> orders) {
        return orders.stream()
                .mapToDouble(Order::getSum)
                .average()
                .orElse(0.);
    }

    private static double GetMaxOrder(List<Order> orders) {
        return orders.stream()
                .mapToDouble(Order::getSum)
                .max()
                .orElse(0.);
    }


    private static void CountStatistics(DailyStats stats, List<Order> orders) {
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
