package ru.job4j.grabber.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class SqlRuDateTimeParser implements DateTimeParser {

    private static final String TODAY = "сегодня";
    private static final String YESTERDAY = "вчера";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d MM yy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private static final Map<String, String> MONTHS = Map.ofEntries(
            Map.entry("янв", "01"),
            Map.entry("фев", "02"),
            Map.entry("мар", "03"),
            Map.entry("апр", "04"),
            Map.entry("май", "05"),
            Map.entry("июн", "06"),
            Map.entry("июл", "07"),
            Map.entry("авг", "08"),
            Map.entry("сен", "09"),
            Map.entry("окт", "10"),
            Map.entry("ноя", "11"),
            Map.entry("дек", "12")
    );

    @Override
    public LocalDateTime parse(String sqlDate) {
        String[] data = sqlDate.split(", ");
        String[] time = data[0].split(" ");
        LocalDate localDate;
        LocalTime localTime = LocalTime.parse(data[1], TIME_FORMATTER);
        if (TODAY.equals(time[0])) {
            localDate = LocalDate.now();
        } else if (YESTERDAY.equals(time[0])) {
            localDate = LocalDate.now().minusDays(1);
        } else {
            localDate = LocalDate.parse(String.format("%s %s %s",
                    time[0], MONTHS.get(time[1]), time[2]), DATE_FORMATTER);
        }
        return LocalDateTime.of(localDate, localTime);
    }
}
