package org.polymath.noteapi.dto.response;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record NoteResponse(UUID id, String title, String content, LocalDateTime modifiedAt, Set<String> tags,boolean archived) {
}
