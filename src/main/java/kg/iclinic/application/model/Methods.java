package kg.iclinic.application.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.*;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class Methods {
    public static final  StatsPeriod monthStats = new StatsPeriod((month) -> month.withDayOfMonth(1),
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

    public static final StatsPeriod yearStats = new StatsPeriod((lastDay) -> lastDay.withDayOfYear(1),
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

    public static Date getTodaysDate(){
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Date today = new Date();
        try {
            return formatter.parse(formatter.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return today;
    }

    public static LocalDate GetFirstDayOfTheWeek() {
        int daysFromWeekStart = LocalDate.now().getDayOfWeek().getValue();
        return LocalDate.now().minusDays(daysFromWeekStart - 1);
    }
}
