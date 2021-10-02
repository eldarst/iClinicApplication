package kg.iclinic.application.controller;

import javafx.util.Pair;
import kg.iclinic.application.entity.Doctor;
import kg.iclinic.application.entity.Order;
import kg.iclinic.application.model.StatsPeriod;
import kg.iclinic.application.service.DailyStatsService;
import kg.iclinic.application.service.DoctorService;
import kg.iclinic.application.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Function;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.nextOrSame;
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

    private static Function<LocalDate, Date> parseLocal = java.sql.Date::valueOf;

    private static StatsPeriod monthStats = new StatsPeriod((month) -> month.withDayOfMonth(1),
            (periodStart) -> periodStart.with(nextOrSame(SUNDAY)),
            (period) -> period.plusDays(1),
            (date) -> {
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                return date.get(weekFields.weekOfMonth());
            },
            (date) -> date.getDayOfWeek().getValue(),
            (date) -> date.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru")),
            0,
            new ArrayList<>(Arrays.asList("Неделя 1", "Неделя 2", "Неделя 3", "Неделя 4", "Неделя 5", "Неделя 6")),
            new ArrayList<>(Arrays.asList("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")),
            "Месяц" ,
             "Неделя",
             "День недели");

    private static StatsPeriod yearStats = new StatsPeriod((lastDay) -> lastDay.withDayOfYear(1),
            (periodStart) -> periodStart.withDayOfMonth(periodStart.lengthOfMonth()),
            (period) -> period.plusWeeks(1),
            LocalDate::getMonthValue,
            (date) -> {
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                return date.get(weekFields.weekOfMonth());
            },
            (date) -> date.getYear() + "-год",
            6,
            new ArrayList<>(Arrays.asList("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")),
            new ArrayList<>(Arrays.asList("Неделя 1", "Неделя 2", "Неделя 3", "Неделя 4", "Неделя 5", "Неделя 6")),
            "Год",
            "Месяц",
            "Неделя");

    @GetMapping("/showDetailsOfOrderList")
    public String showDetailsOfTodayOrderList(@RequestParam(value = "dayBefore", required = false) Integer dayBeforeCount,
                                              @RequestParam(value = "dayAfter", required = false) Integer dayAfterCount,
                                              Model theModel) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(
                "MM/dd/yyyy", Locale.US);
        int dayRatio = (dayAfterCount != null && dayBeforeCount != null) ? dayAfterCount - dayBeforeCount : 0;
        if (dayRatio != 0) {
            Date day = parseLocal.apply(LocalDate.now().plusDays(dayRatio));
            theModel.addAttribute("stats", dailyStatsService.getStatsByDate(day, day));
            theModel.addAttribute("day", day);
        } else {
            Date day = parseLocal.apply(LocalDate.now());
            theModel.addAttribute("stats", dailyStatsService.getStatistics(orderService.getTodayOrders(), day, day));
            theModel.addAttribute("day", new Date());
        }
        theModel.addAttribute("dayBefore", (dayBeforeCount != null) ? dayBeforeCount : 0);
        theModel.addAttribute("dayAfter", (dayAfterCount != null) ? dayAfterCount : 0);
        return "details-list";
    }

    @GetMapping("/showDetailsOfOrderListMonthly")
    public String showDetailsOfTodayOrderListMonthly(@RequestParam(value = "periodBefore", required = false) Integer periodBeforeCount,
                                                     @RequestParam(value = "periodAfter", required = false) Integer periodAfterCount,
                                                     @RequestParam(value = "period", required = false, defaultValue = "month") String period,
                                                     Model theModel) throws ParseException {
        StatsPeriod periodStats = (period.equals("month")) ? monthStats : yearStats;

        DateFormat dateFormat = new SimpleDateFormat(
                "MM/dd/yyyy", Locale.US);

        int periodRatio = (periodBeforeCount != null && periodAfterCount != null) ? periodAfterCount - periodBeforeCount : 0;
        LocalDate now = LocalDate.now();
        LocalDate lastDay = (periodRatio != 0)
                ? (period.equals("month"))
                    ? now.plusMonths(periodRatio)
                    .withDayOfMonth(now.plusMonths(periodRatio).lengthOfMonth())
                    : LocalDate.now().plusYears(periodRatio).withDayOfYear(now.plusYears(periodRatio).lengthOfYear())
                : now;
        Date lastDayOfPeriod = parseLocal.apply(lastDay);
        Date firstDayOfPeriod = parseLocal.apply(periodStats.getFirstDayOfAllPeriod().apply(lastDay));

        theModel.addAttribute("period", period);
        theModel.addAttribute("periodTitles", Arrays.asList(periodStats.getMainPeriodTitle(), periodStats.getPeriodTitle(), periodStats.getSubPeriodTitle()));
        theModel.addAttribute("periodName", periodStats.getMainPeriodTitleValue().apply(lastDay));
        theModel.addAttribute("stats", dailyStatsService.getPeriodStats(lastDayOfPeriod, periodStats));
        theModel.addAttribute("lastDay", lastDayOfPeriod);
        theModel.addAttribute("firstDay", firstDayOfPeriod);
        theModel.addAttribute("periodStats", dailyStatsService.getStatsByDate(firstDayOfPeriod, lastDayOfPeriod));
        theModel.addAttribute("periodBefore", (periodBeforeCount != null) ? periodBeforeCount : 0);
        theModel.addAttribute("periodAfter", (periodAfterCount != null) ? periodAfterCount : 0);
        return "weekly-monthly-stats";
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
                                          Model theModel) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(
                "MM/dd/yyyy", Locale.US);
        Date firstDate = new Date();
        Date secondDate = new Date();
        if (dateFrom.length() > 0 && dateTo.length() > 0) {
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
        return parseLocal.apply(monday);
    }

    private HashMap<String, Pair<Double, Integer>> GetMappedSalary(Date dateFrom, Date dateTo) {
        List<Doctor> doctors = doctorService.findListOfDoctors();
        HashMap<String, Pair<Double, Integer>> result = new HashMap<>();
        for (Doctor doctor : doctors) {
            List<Order> sortedOrders = orderService.getSortedOrders(dateFrom, dateTo, doctor.getName());
            if (sortedOrders.size() > 0)
                result.put(doctor.getName(),
                        new Pair<>((double) Math.round(orderService.countSalary(sortedOrders) * 0.2), sortedOrders.size()));
        }
        return result;
    }

}