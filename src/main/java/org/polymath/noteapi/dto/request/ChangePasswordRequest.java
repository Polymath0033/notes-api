package org.polymath.noteapi.dto.request;

public record ChangePasswordRequest(String oldPassword, String newPassword) {
}
