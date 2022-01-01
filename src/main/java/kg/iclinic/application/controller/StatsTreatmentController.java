package kg.iclinic.application.controller;

import kg.iclinic.application.model.Methods;
import kg.iclinic.application.service.TreatmentStatsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.function.Function;

@Controller
@RequestMapping("/trtstats")
public class StatsTreatmentController {

    private final TreatmentStatsService treatmentStatsService;

    public StatsTreatmentController(TreatmentStatsService treatmentStatsService) {
        this.treatmentStatsService = treatmentStatsService;
    }

    private static final Function<LocalDate, Date> parseLocal = java.sql.Date::valueOf;


    @GetMapping("/showDetailsOfTreatmentListMonthly")
    public String showDetailsOfTodayOrderListByPeriod(@RequestParam(value = "periodAdd", required = false) Integer periodAdd,
                                                     @RequestParam(value = "period", required = false, defaultValue = "week") String period,
                                                     Model theModel) throws ParseException {
        int periodAmountToAdd = (periodAdd != null) ? periodAdd : 0;
        LocalDate now = LocalDate.now();
        LocalDate lastDay = (period.equals("month"))
                ? now.plusMonths(periodAmountToAdd).withDayOfMonth(now.plusMonths(periodAmountToAdd).lengthOfMonth())
                : Methods.GetFirstDayOfTheWeek().plusWeeks(periodAmountToAdd).plusDays(6);
        LocalDate firstDay = (period.equals("month"))
                ? now.plusMonths(periodAmountToAdd).withDayOfMonth(1)
                : Methods.GetFirstDayOfTheWeek().plusWeeks(periodAmountToAdd);
        Date lastDayOfPeriod = parseLocal.apply(lastDay);
        Date firstDayOfPeriod = parseLocal.apply(firstDay);


        theModel.addAttribute("theListOfTreatmentVisits", treatmentStatsService.getStatsByDate(firstDayOfPeriod, lastDayOfPeriod));
        theModel.addAttribute("period", period);
        theModel.addAttribute("periodAdd", periodAmountToAdd);
        theModel.addAttribute("firstDay", firstDayOfPeriod);
        theModel.addAttribute("lastDay", lastDayOfPeriod);
        return "treatment-period-statistics";
    }
}
