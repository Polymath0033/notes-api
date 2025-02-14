package org.polymath.noteapi.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class CalculateExpiresInSeconds {
    public static long calculateTimeInSeconds(LocalDateTime dateTime) {
        return Math.max(0,dateTime.toEpochSecond(ZoneOffset.UTC)-LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
    }
}
