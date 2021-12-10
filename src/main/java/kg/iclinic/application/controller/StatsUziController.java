package kg.iclinic.application.controller;

import javafx.util.Pair;
import kg.iclinic.application.entity.Doctor;
import kg.iclinic.application.entity.Order;
import kg.iclinic.application.model.StatsPeriod;
import kg.iclinic.application.service.DailyStatsService;
import kg.iclinic.application.service.DoctorService;
import kg.iclinic.application.service.OrderService;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
public class StatsUziController {

    @Autowired
    OrderService orderService;

    @Autowired
    DoctorService doctorService;

    @Autowired
    DailyStatsService dailyStatsService;

    private static final Function<LocalDate, Date> parseLocal = java.sql.Date::valueOf;

    private static final  StatsPeriod monthStats = new StatsPeriod((month) -> month.withDayOfMonth(1),
            (periodStart) -> {
                LocalDate nextSunday = periodStart.with(nextOrSame(SUNDAY));
                return nextSunday.isAfter(periodStart) || nextSunday.equals(periodStart) ? nextSunday : nextSunday.withDayOfMonth(nextSunday.lengthOfMonth());
            },
            (period) -> period.plusDays(1),
            (date) -> {
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                return date.get(weekFields.weekOfMonth());
            },
            (date) -> date.getDayOfWeek().getValue(),
            (date) -> date.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru")),
            (date) -> 0,
            new ArrayList<>(Arrays.asList("Неделя 1", "Неделя 2", "Неделя 3", "Неделя 4", "Неделя 5", "Неделя 6")),
            new ArrayList<>(Arrays.asList("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")),
            "Месяц" ,
             "Неделя",
             "День недели");

    private static final StatsPeriod yearStats = new StatsPeriod((lastDay) -> lastDay.withDayOfYear(1),
            (periodStart) -> periodStart.withDayOfMonth(periodStart.lengthOfMonth()),
            (period) -> {
                LocalDate nextMonday = period.plusWeeks(1).with(previousOrSame(MONDAY));
                return nextMonday.isAfter(period) ? nextMonday : nextMonday.withDayOfMonth(nextMonday.lengthOfMonth());
            },
            LocalDate::getMonthValue,
            (date) -> {
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                return date.get(weekFields.weekOfMonth());
            },
            (date) -> date.getYear() + "-год",
            (date) -> {
                int ratio = date.with(nextOrSame(SUNDAY)).getDayOfMonth() - date.getDayOfMonth();
                return ratio >= 0 ? ratio : date.withDayOfYear(date.lengthOfMonth()).getDayOfMonth() - date.getDayOfMonth();
            },
            new ArrayList<>(Arrays.asList("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")),
            new ArrayList<>(Arrays.asList("Неделя 1", "Неделя 2", "Неделя 3", "Неделя 4", "Неделя 5", "Неделя 6")),
            "Год",
            "Месяц",
            "Неделя");

    @GetMapping("/showDetailsOfOrderList")
    public String showDetailsOfTodayOrderList(@RequestParam(value = "daysAdd", required = false) Integer daysAdd,
                                              Model theModel) throws ParseException {
        int daysToAdd = (daysAdd != null) ? daysAdd : 0;
        Date day = parseLocal.apply(LocalDate.now().plusDays(daysToAdd));

        theModel.addAttribute("stats", dailyStatsService.getStatsByDate(day, day));
        theModel.addAttribute("day", day);
        theModel.addAttribute("daysAdd", daysToAdd);
        return "details-list";
    }

    @GetMapping("/showDetailsOfOrderListMonthly")
    public String showDetailsOfTodayOrderListMonthly(@RequestParam(value = "periodAdd", required = false) Integer periodAdd,
                                                     @RequestParam(value = "period", required = false, defaultValue = "month") String period,
                                                     Model theModel) throws ParseException {
        StatsPeriod periodStats = (period.equals("month")) ? monthStats : yearStats;
        int periodAmountToAdd = (periodAdd != null) ? periodAdd : 0;
        LocalDate now = LocalDate.now();
        LocalDate lastDay = (period.equals("month"))
                    ? now.plusMonths(periodAmountToAdd).withDayOfMonth(now.plusMonths(periodAmountToAdd).lengthOfMonth())
                    : now.plusYears(periodAmountToAdd).withDayOfYear(now.plusYears(periodAmountToAdd).lengthOfYear());
        Date lastDayOfPeriod = parseLocal.apply(lastDay);
        Date firstDayOfPeriod = parseLocal.apply(periodStats.getFirstDayOfAllPeriod().apply(lastDay));

        theModel.addAttribute("periodTitles", Arrays.asList(periodStats.getMainPeriodTitle(), periodStats.getPeriodTitle(), periodStats.getSubPeriodTitle()));
        theModel.addAttribute("periodName", periodStats.getMainPeriodTitleValue().apply(lastDay));
        theModel.addAttribute("firstDay", firstDayOfPeriod);
        theModel.addAttribute("lastDay", lastDayOfPeriod);
        theModel.addAttribute("stats", dailyStatsService.getPeriodStats(lastDayOfPeriod, periodStats));
        theModel.addAttribute("totalPeriodStats", dailyStatsService.getStatsByDate(firstDayOfPeriod, lastDayOfPeriod));
        theModel.addAttribute("period", period);
        theModel.addAttribute("periodAdd", periodAmountToAdd);
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
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        Date firstDate = (dateFrom.length() > 0) ? dateFormat.parse(dateFrom) : new Date();
        Date secondDate = (dateTo.length() > 0) ? dateFormat.parse(dateTo) : new Date();

        theModel.addAttribute("theListOfDoctorsWithSalaries", GetMappedSalary(firstDate, secondDate));
        theModel.addAttribute("dateFrom", firstDate);
        theModel.addAttribute("dateTo", secondDate);
        return "salary-by-date";
    }

    @GetMapping("/listCurrentWeekSalary")
    public String getCurrentWeekSalaries(@RequestParam(value = "weekAdd", required = false) Integer weekAdd,
                                         Model theModel) {
        int weeksAdded = weekAdd != null ? weekAdd : 0;
        LocalDate firstDayOfWeek = GetFirstDayOfTheWeek().plusDays(weeksAdded * 7);
        Date parsedFirstDay = parseLocal.apply(firstDayOfWeek);
        Date parsedLastDay = parseLocal.apply(firstDayOfWeek.plusDays(6));

        theModel.addAttribute("theListOfDoctorsWithSalaries", GetMappedSalary(parsedFirstDay, parsedLastDay));
        theModel.addAttribute("weekStart", parsedFirstDay);
        theModel.addAttribute("weekEnd", parsedLastDay);
        theModel.addAttribute("weekAdd", weeksAdded);
        return "current-week-salary";
    }

    private LocalDate GetFirstDayOfTheWeek() {
        int daysFromWeekStart = LocalDate.now().getDayOfWeek().getValue();
        return LocalDate.now().minusDays(daysFromWeekStart - 1);
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