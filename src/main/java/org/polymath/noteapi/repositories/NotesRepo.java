package org.polymath.noteapi.repositories;

import org.polymath.noteapi.models.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotesRepo extends JpaRepository<Notes, UUID> {
    //public List<Notes> findAllNotes();
    List<Notes> findByTitle(String title);
}
