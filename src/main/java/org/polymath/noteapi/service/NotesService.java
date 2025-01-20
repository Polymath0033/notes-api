package org.polymath.noteapi.service;

import org.polymath.noteapi.models.Notes;
import org.polymath.noteapi.models.Users;
import org.polymath.noteapi.repositories.NotesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotesService {

    @Autowired
    private NotesRepo notesRepo;

//    public void addNotes(Notes notes, UUID id){
//        Users users = notesRepo.findNoteById(id).getUser();
//    }
}
