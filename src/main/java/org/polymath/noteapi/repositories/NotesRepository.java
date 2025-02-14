package org.polymath.noteapi.repositories;

import org.polymath.noteapi.models.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotesRepository extends JpaRepository<Notes, UUID> {
    Optional<Notes> findNotesByIdAndUserEmail(UUID id, String email);
    Optional<List<Notes>> findAllByUserEmail(String email);
    Optional<List<Notes>> findAllByUserEmailAndTitleContainsIgnoreCase(String email, String title);
    @Query("""
select note from Notes note where note.user.email=:email and note.archived=true
""")
    Optional<List<Notes>> findAllByUserEmailAndArchived(String email);

    @Query("""
    select note from Notes note
    where note.user.email = :email
    and (
        lower(note.title) like lower(concat('%', :search, '%'))
        or lower(note.content) like lower(concat('%', :search, '%'))
        or :search member of note.tag
    )
""")
    Optional<List<Notes>> findAllBySearchTitleOrContentOrTag(String search,String email);
}