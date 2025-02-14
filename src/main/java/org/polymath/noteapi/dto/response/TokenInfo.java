package org.polymath.noteapi.dto.response;

import java.time.LocalDateTime;

public record TokenInfo(String authToken, LocalDateTime tokenExpiration) {
}
