package kg.iclinic.application.model;

import kg.iclinic.application.entity.DailyStats;
import lombok.*;

import java.util.LinkedHashMap;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Statistics {
    private String periodName;
    private DailyStats periodTotalStats;
    private LinkedHashMap<String, DailyStats> periodStatsDetails;
}
