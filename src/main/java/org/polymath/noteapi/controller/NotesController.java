package org.polymath.noteapi.controller;

import org.polymath.noteapi.models.Notes;
import org.polymath.noteapi.models.Users;
import org.polymath.noteapi.repositories.NotesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin
public class NotesController {
    @Autowired
    private NotesRepo notesRepo;

    @GetMapping("")
    public String fromNotes(){
        return "Notes";
    }
    //public List<Notes> getNotes() {
     //   return notesRepo.findAll();
   // }
    @PostMapping("/save")
    public Notes saveNote(@RequestBody Notes note,@RequestParam UUID userId) {
        Users user = notesRepo.findById(userId).orElseThrow(()-> new RuntimeException("User not found")).getUser();
        note.setUser(user);
        note.setLastModified(LocalDateTime.now());
        return notesRepo.save(note);
    }
}
