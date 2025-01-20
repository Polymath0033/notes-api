package org.polymath.noteapi.controller;

import org.polymath.noteapi.models.Notes;
import org.polymath.noteapi.models.Users;
import org.polymath.noteapi.repositories.NotesRepo;
import org.polymath.noteapi.repositories.UserRepo;
import org.polymath.noteapi.util.ApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin
public class NotesController {
    @Autowired
    private NotesRepo notesRepo;

    @Autowired
    private UserRepo userRepo;

    @GetMapping("")
    public List<Notes> getNotes() {
        return notesRepo.findAll();
    }
//    @GetMapping("")
//    public ResponseEntity<?> getAllUserNotes(@PathVariable UUID userId) {
//        try {
//            Optional<Notes> notes = notesRepo.findAllByUserId(userId);
//            if (notes.isPresent()) {
//                return new ResponseEntity<>(notes.get(), HttpStatus.OK);
//            }else {
//                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null));
//        }
//
//    }
    @PostMapping("/save")
    public Notes saveNote(@RequestBody Notes note) {
        UUID userId = note.getUserId();
        Users user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));
        note.setUserId(userId);
        note.setAuthor(user.getFirstName()+" "+user.getLastName());
        return notesRepo.save(note);
    }


    @GetMapping("/{id}")
    public Optional<Notes> getNotesById(@PathVariable UUID id) {
        return notesRepo.findAllById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchAllByTitle(@RequestParam String title) {
       try {
           if(title==null || title.isEmpty()) {
               return ResponseEntity.badRequest().body(new ApiError(HttpStatus.BAD_REQUEST,"title must be null or empty",null));
           }
           Optional<Notes> notes = notesRepo.findAllByTitleIsContainingIgnoreCase(title);
           if(notes.isPresent()) {
               return ResponseEntity.ok(notes.get());
           }else {
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(HttpStatus.NOT_FOUND,"No note(s) found for this title",null));
           }
       }catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage(),null));
       }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateNote(@RequestBody Notes note, @PathVariable UUID id) {
        try{
            Notes existingNote = notesRepo.findNoteById(id);
            if(existingNote == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(HttpStatus.NOT_FOUND,"Note not found",null));
            }
            if(note.getUserId()==null || note.getTitle()==null || note.getTitle().isEmpty()||note.getContent()==null || note.getContent().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiError(HttpStatus.BAD_REQUEST,"Recheck what you are sending",null));
            }
            existingNote.setTitle(note.getTitle());
            existingNote.setContent(note.getContent());
            existingNote.setTags(note.getTags());
            notesRepo.save(existingNote);
            return ResponseEntity.ok(existingNote);
        } catch (Exception e) {
           return ResponseEntity.badRequest().body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage(),null));
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable UUID id) {
        try {
           notesRepo.deleteById(id);
           return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage(),null));
        }
    }
}
