package kg.iclinic.application.controller;

import kg.iclinic.application.entity.Consultation;
import kg.iclinic.application.entity.DoctorCons;
import kg.iclinic.application.entity.Order;
import kg.iclinic.application.model.ConsultationStats;
import kg.iclinic.application.model.Methods;
import kg.iclinic.application.service.ConsultationService;
import kg.iclinic.application.service.DoctorConsService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

@Controller
@RequestMapping("/cons")
public class StatsConsultController {
    private final ConsultationService consultationService;

    private final DoctorConsService doctorConsService;

    private static final DateFormat dateFormat = new SimpleDateFormat(
            "MM/dd/yyyy", Locale.US);

    private static final Function<LocalDate, Date> parseLocal = java.sql.Date::valueOf;

    public StatsConsultController(ConsultationService consultationService, DoctorConsService doctorConsService) {
        this.consultationService = consultationService;
        this.doctorConsService = doctorConsService;
    }

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


    @GetMapping("/showSortByDateForm")
    public String showSortByDateForm(Model theModel) {
        theModel.addAttribute("dateFrom", new Date());
        theModel.addAttribute("dateTo", new Date());
        theModel.addAttribute("doctorName", "");
        theModel.addAttribute("theListOfDoctor", doctorConsService.findAllDoctors());
        theModel.addAttribute("theListOfPatients", new ArrayList<Order>());
        return "consultations-between-dates";
    }

    @GetMapping("/listOrderBetweenDates")
    public String getOrdersBetweenDates(@RequestParam(value = "dateFrom", required = false) String dateFrom,
                                        @RequestParam(value = "dateTo", required = false) String dateTo,
                                        @RequestParam(value = "doctorId", required = false) Long doctorId,
                                        Model theModel) throws ParseException {
        Date firstDate = dateFrom != null ? dateFormat.parse(dateFrom) : new Date();
        Date secondDate = dateTo != null ? dateFormat.parse(dateTo) : new Date();
        DoctorCons doctor = null;
        if (doctorId != null)
            doctor = doctorConsService.findDoctor(doctorId);
        List<Consultation> sortedOrders = consultationService.getDoctorConsultationsBetweenDates(firstDate, secondDate, doctor);

        theModel.addAttribute("theListOfDoctor", doctorConsService.findAllDoctors());
        theModel.addAttribute("theListOfPatients", sortedOrders);
        theModel.addAttribute("dateFrom", firstDate);
        theModel.addAttribute("dateTo", secondDate);
        theModel.addAttribute("doctorName", doctor != null ? doctor.getName() : "");
        return "consultations-between-dates";
    }

    @GetMapping("/listCurrentWeekConsultations")
    public String getCurrentWeekSalaries(@RequestParam(value = "doctorId", required = false) Long doctorId,
                                         @RequestParam(value = "weekAdd", required = false) Integer weekAdd,
                                         Model theModel) {
        int weeksAdded = weekAdd != null ? weekAdd : 0;
        DoctorCons doctor = null;
        if (doctorId != null)
            doctor = doctorConsService.findDoctor(doctorId);

        LocalDate firstDayOfWeek = Methods.GetFirstDayOfTheWeek().plusDays(weeksAdded * 7L);
        Date parsedFirstDay = parseLocal.apply(firstDayOfWeek);
        Date parsedLastDay = parseLocal.apply(firstDayOfWeek.plusDays(6));

        List<Consultation> consultationList = null;

        if (doctor != null) {
            consultationList = consultationService.getDoctorConsultationsBetweenDates(parsedFirstDay, parsedLastDay, doctor);
        } else {
            consultationList = consultationService.getConsultationsBetweenDates(parsedFirstDay, parsedLastDay);
        }

        theModel.addAttribute("theListOfDoctor", doctorConsService.findAllDoctors());
        theModel.addAttribute("theListOfPatients", consultationList);
        theModel.addAttribute("weekStart", parsedFirstDay);
        theModel.addAttribute("weekEnd", parsedLastDay);
        theModel.addAttribute("weekAdd", weeksAdded);
        theModel.addAttribute("doctorId", doctorId);

        return "consultations-of-week";
    }

}
