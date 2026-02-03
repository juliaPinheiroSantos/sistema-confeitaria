package app;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Logs application flow events to stdout so you can see the flow in console/Docker logs.
 * Prefix [FLOW] makes it easy to grep (e.g. {@code docker logs ... 2>&1 | grep FLOW}).
 * Used by Main and controllers to trace navigation and actions.
 */
public final class FlowHandler {

    private static final String PREFIX = "[FLOW] ";
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .withZone(ZoneId.systemDefault());

    private FlowHandler() {}

    /**
     * Logs a flow event.
     *
     * @param event event name (e.g. APP_START, HOME_SHOWN, REGISTER_OPENED)
     */
    public static void log(String event) {
        emit(event, (String[]) null);
    }

    /**
     * Logs a flow event with one detail.
     *
     * @param event  event name
     * @param detail optional detail (e.g. error message or "simulated")
     */
    public static void log(String event, String detail) {
        emit(event, detail == null ? null : new String[]{detail});
    }

    /**
     * Logs a flow event with multiple details (joined by " | ").
     */
    public static void log(String event, String... details) {
        emit(event, details);
    }

    private static void emit(String event, String... details) {
        String timestamp = TIME_FMT.format(Instant.now());
        StringBuilder line = new StringBuilder()
                .append(PREFIX)
                .append(timestamp)
                .append(" ")
                .append(event);
        if (details != null) {
            for (String d : details) {
                if (d != null && !d.isEmpty()) {
                    line.append(" | ").append(d);
                }
            }
        }
        System.out.println(line);
        System.out.flush();
    }
}
