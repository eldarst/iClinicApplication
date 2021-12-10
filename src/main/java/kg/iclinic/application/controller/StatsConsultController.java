package kg.iclinic.application.controller;

import kg.iclinic.application.entity.Consultation;
import kg.iclinic.application.model.ConsultationStats;
import kg.iclinic.application.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Controller
@RequestMapping("/cons")
public class StatsConsultController {
    @Autowired
    ConsultationService consultationService;

    private static final Function<LocalDate, Date> parseLocal = java.sql.Date::valueOf;

    @GetMapping("/showPeriodStats")
    public String showWeekStats(@RequestParam(value = "periodAdd", required = false) Integer periodAdd,
                                @RequestParam(value = "period", required = false, defaultValue = "week") String period,
                                Model theModel) {
        int periodCount = periodAdd != null ? periodAdd : 0;
        Date parsedPeriodStart;
        Date parsedPeriodEnd;
        if (period.equals("week")) {
            LocalDate firstDay = GetFirstDayOfTheWeek().plusDays(periodCount * 7);
            parsedPeriodStart = parseLocal.apply(firstDay);
            parsedPeriodEnd = parseLocal.apply(firstDay.plusDays(6));
        } else {
            LocalDate firstDay = LocalDate.now().plusMonths(periodCount).withDayOfMonth(1);
            parsedPeriodStart = parseLocal.apply(firstDay);
            parsedPeriodEnd = parseLocal.apply(firstDay.withDayOfMonth(firstDay.lengthOfMonth()));
        }
        List<Consultation> consultations = consultationService.getConsultationsBetweenDates(parsedPeriodStart, parsedPeriodEnd);

        theModel.addAttribute("theListOfConsultationStats", ConsultationStats.GetConsultationStats(consultations));
        theModel.addAttribute("periodStart", parsedPeriodStart);
        theModel.addAttribute("periodEnd", parsedPeriodEnd);

        theModel.addAttribute("periodAdd", periodCount);
        theModel.addAttribute("period", period);
        return "consultation-stats";
    }

    private LocalDate GetFirstDayOfTheWeek() {
        int daysFromWeekStart = LocalDate.now().getDayOfWeek().getValue();
        return LocalDate.now().minusDays(daysFromWeekStart - 1);
    }


}
