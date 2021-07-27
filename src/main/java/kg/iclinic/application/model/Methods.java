package kg.iclinic.application.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Methods {
    public static Date getTodaysDate(){
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Date today = new Date();

        try {
            Date todayWithZeroTime = formatter.parse(formatter.format(today));
            return todayWithZeroTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return today;
    }
}
