package no.nofuzz.gs1.ai;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public final class StandardAis {

    private static final DateTimeFormatter YYMMDD =
            DateTimeFormatter.ofPattern("yyMMdd");

    private StandardAis() {}

    public static AiRegistry defaultRegistry() {
        return new AiRegistry(Map.ofEntries(

                Map.entry("01", new ApplicationIdentifier(
                        "01", 14, 14, false, v -> v)),

                Map.entry("10", new ApplicationIdentifier(
                        "10", null, 20, true, v -> v)),

                Map.entry("17", new ApplicationIdentifier(
                        "17", 6, 6, false,
                        v -> LocalDate.parse(v, YYMMDD))),

                Map.entry("21", new ApplicationIdentifier(
                        "21", null, 20, true, v -> v)),

                Map.entry("30", new ApplicationIdentifier(
                        "30", null, 8, true, Integer::parseInt))
        ));
    }
}
