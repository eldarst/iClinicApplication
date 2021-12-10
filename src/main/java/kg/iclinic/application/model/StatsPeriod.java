package kg.iclinic.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.function.Function;

@Data
@AllArgsConstructor
public class StatsPeriod {
    private Function<LocalDate, LocalDate> firstDayOfAllPeriod;

    private Function<LocalDate, LocalDate> endOfSubPeriod;

    private Function<LocalDate, LocalDate> subPeriodIncrementing;

    private Function<LocalDate, Integer> periodCount;

    private Function<LocalDate, Integer> SubPeriodCount;

    private Function<LocalDate, String> mainPeriodTitleValue;

    private Function<LocalDate, Integer> subPeriodLastDay;

    private ArrayList<String> periodNames;

    private ArrayList<String> subPeriodNames;

    private String mainPeriodTitle;

    private String periodTitle;

    private String subPeriodTitle;

}
