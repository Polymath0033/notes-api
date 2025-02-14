package org.polymath.noteapi.dto.response;

import org.springframework.http.HttpStatus;

public record ErrorResponse(String message, HttpStatus status, long timestamp) {
}
