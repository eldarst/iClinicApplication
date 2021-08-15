package kg.iclinic.application.controller;

import javafx.util.Pair;
import kg.iclinic.application.entity.Doctor;
import kg.iclinic.application.entity.Order;
import kg.iclinic.application.entity.Product;
import kg.iclinic.application.service.DailyStatsService;
import kg.iclinic.application.service.DoctorService;
import kg.iclinic.application.service.OrderService;
import kg.iclinic.application.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

@Controller
@RequestMapping("/uzi")
public class StatsController {

    @Autowired
    OrderService orderService;

    @Autowired
    DoctorService doctorService;

    @Autowired
    DailyStatsService dailyStatsService;

    @GetMapping("/showDetailsOfOrderList")
    public String showDetailsOfTodayOrderList(@RequestParam(value = "dayBefore", required = false) Integer dayBeforeCount,
                                              @RequestParam(value = "dayAfter", required = false) Integer dayAfterCount,
                                              Model theModel) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(
                "MM/dd/yyyy", Locale.US);
        int dayRatio = 0;
        if(dayAfterCount != null && dayBeforeCount != null)
            dayRatio = dayAfterCount - dayBeforeCount;
        if(dayRatio > 0) {
            Date day = java.sql.Date.valueOf(LocalDate.now().plusDays(Math.abs(dayRatio)));
            theModel.addAttribute("stats", dailyStatsService.getStatsByDate(day));
            theModel.addAttribute("day", day);
        }
        else if(dayRatio < 0) {
            Date day = java.sql.Date.valueOf(LocalDate.now().minusDays(Math.abs(dayRatio)));
            theModel.addAttribute("stats", dailyStatsService.getStatsByDate(day));
            theModel.addAttribute("day", day);
        }
        else {
            theModel.addAttribute("stats", dailyStatsService.getStatistics(orderService.getTodayOrders()));
            theModel.addAttribute("day", new Date());
        }
        theModel.addAttribute("dayBefore", (dayBeforeCount != null) ? dayBeforeCount : 0);
        theModel.addAttribute("dayAfter", (dayAfterCount != null) ? dayAfterCount : 0);
        return "details-list";
    }


    @GetMapping("/showSalariesBetweenDatesForm")
    public String showSalariesBetweenDatesForm(Model theModel) {
        theModel.addAttribute("dateFrom", new Date());
        theModel.addAttribute("dateTo", new Date());
        return "salary-by-date";
    }

    @RequestMapping("/listSalariesBetweenDates")
    public String getSalariesBetweenDates(@RequestParam(value = "dateFrom", required = false) String dateFrom,
                                          @RequestParam(value = "dateTo", required = false) String dateTo,
                                          Model theModel) throws ParseException{
        DateFormat dateFormat = new SimpleDateFormat(
                "MM/dd/yyyy", Locale.US);
        Date firstDate = new Date();
        Date secondDate = new Date();
        if(dateFrom.length() > 0 && dateTo.length() > 0) {
            firstDate = dateFormat.parse(dateFrom);
            secondDate = dateFormat.parse(dateTo);
        }
        HashMap<String, Pair<Double, Integer>> doctorsSalaries = GetMappedSalary(firstDate, secondDate);

        theModel.addAttribute("theListOfDoctorsWithSalaries", doctorsSalaries);
        theModel.addAttribute("dateFrom", firstDate);
        theModel.addAttribute("dateTo", secondDate);
        return "salary-by-date";
    }

    @GetMapping("/listCurrentWeekSalary")
    public String getCurrentWeekSalaries(Model theModel) {
        Date firstDayOfWeek = GetFirstDayOfTheWeek();
        System.out.println(firstDayOfWeek);
        HashMap<String, Pair<Double, Integer>> doctorsSalaries = GetMappedSalary(firstDayOfWeek, new Date());
        theModel.addAttribute("theListOfDoctorsWithSalaries", doctorsSalaries);
        return "current-week-salary";
    }

    private Date GetFirstDayOfTheWeek() {
        LocalDate today = LocalDate.now();

        LocalDate monday = today.with(previousOrSame(MONDAY));
        //LocalDate sunday = today.with(nextOrSame(SUNDAY));
        return java.sql.Date.valueOf(monday);
    }

    private HashMap<String, Pair<Double, Integer>> GetMappedSalary(Date dateFrom, Date dateTo) {
        List<Doctor> doctors = doctorService.findListOfDoctors();
        HashMap<String, Pair<Double, Integer>> result = new HashMap<>();
        for(Doctor doctor: doctors) {
            List<Order> sortedOrders = orderService.getSortedOrders(dateFrom, dateTo, doctor.getName());
            if(sortedOrders.size() > 0)
                result.put(doctor.getName(),
                        new Pair<>((double) Math.round(orderService.countSalary(sortedOrders) * 0.2), sortedOrders.size()));
        }
        return result;
    }

}