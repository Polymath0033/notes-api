package org.polymath.noteapi.repositories;

import jakarta.persistence.NamedQuery;
import org.polymath.noteapi.models.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotesRepo extends JpaRepository<Notes, UUID> {
    //public List<Notes> findAllNotes();
    List<Notes> findByTitle(String title);
    Notes findNoteById(UUID id);
    Optional<Notes> findAllById(UUID id);
    List<Notes> findAllByTitleIsContainingIgnoreCase(String title);
    List<Notes> findAllByUserId(UUID userId);
//
//    @Query("select note from Notes note where "+
//            "(lower(note.title) like lower(concat('%',:title,'%')) or :title is null) or "+
//            "(lower(note.content) like lower(concat('%',:content,'%')) or :content is null) or "+
//            "(:tags is null or exists (select tag from note.tags tag where tag in :tags))")
//@Query("""
//    select note from Notes note
//    where (
//        (:title is null or lower(note.title) like lower(concat('%', :title, '%')))
//        and (:content is null or lower(note.content) like lower(concat('%', :content, '%')))
//        and (:tags is null or exists (
//            select tag from note.tags tag where tag in :tags
//        ))
//    )
//""")

    @Query("""
    select note from Notes note
    where (
        (:title is null or lower(cast(note.title as string)) like lower(concat('%', :title, '%')))
        and (:content is null or lower(cast(note.content as string)) like lower(concat('%', :content, '%')))
        and (:tags is null or exists (
            select tag from note.tags tag where tag in :tags
        ))
    )
""")
    List<Notes> findNotesByTitleOrContentOrTags(String title,String content,List<String> tags);

}
