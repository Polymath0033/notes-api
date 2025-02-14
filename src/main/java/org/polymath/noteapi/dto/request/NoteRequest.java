package org.polymath.noteapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import java.util.List;

public record NoteRequest(@NotNull @NotBlank(message = "Title can not be blank") String title, String content, List<String> tags) {
}
