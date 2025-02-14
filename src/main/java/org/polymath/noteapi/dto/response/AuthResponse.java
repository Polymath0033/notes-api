package org.polymath.noteapi.dto.response;

import java.util.UUID;

public record AuthResponse(UUID userId, String email, String token, long tokenExpireIn) {
}
