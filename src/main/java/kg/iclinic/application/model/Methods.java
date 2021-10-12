package kg.iclinic.application.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class Methods {

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
    public static LocalDate GetFirstDayOfTheWeek(LocalDate day) {
        return day.with(previousOrSame(MONDAY));
    }

    public static LocalDate GetLastDayOfTheWeek(LocalDate day) {
        return day.with(previousOrSame(SUNDAY));
    }
}
