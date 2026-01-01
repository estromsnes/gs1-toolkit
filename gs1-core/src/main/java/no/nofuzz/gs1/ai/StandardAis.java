package no.nofuzz.gs1.ai;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public final class StandardAis {

    private static final DateTimeFormatter YYMMDD =
            DateTimeFormatter.ofPattern("yyMMdd");

    private StandardAis() {}

    public static AiRegistry defaultRegistry() {
        Map<String, ApplicationIdentifier> map = new HashMap<>();

        map.put("00", new ApplicationIdentifier("00", 18, false, v -> v));
        map.put("01", new ApplicationIdentifier("01", 14, false, v -> v));
        map.put("10", new ApplicationIdentifier("10", -1, true, v -> v));
        map.put("17", new ApplicationIdentifier("17", 6, false,
                v -> LocalDate.parse(v, YYMMDD)));

        map.put("21", new ApplicationIdentifier("21", -1, true, v -> v));
        map.put("30", new ApplicationIdentifier("30", -1, true, Integer::parseInt));
        map.put("37", new ApplicationIdentifier("37", -1, true, Integer::parseInt));
        map.put("392", new ApplicationIdentifier("392", -1, true, Integer::parseInt));

        return new AiRegistry(map);
    }
}