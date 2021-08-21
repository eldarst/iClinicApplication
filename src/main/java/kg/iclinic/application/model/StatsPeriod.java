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

    private Function<LocalDate, Integer> SubPeriodName;

    private int period;

    ArrayList<String> periodNames;

    ArrayList<String> subPeriodNames;
}
