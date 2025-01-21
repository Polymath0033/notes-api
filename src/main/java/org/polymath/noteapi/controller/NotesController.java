package org.polymath.noteapi.controller;

import io.jsonwebtoken.MalformedJwtException;
import org.polymath.noteapi.models.Notes;
import org.polymath.noteapi.models.Users;
import org.polymath.noteapi.repositories.NotesRepo;
import org.polymath.noteapi.repositories.UserRepo;
import org.polymath.noteapi.service.JWTService;
import org.polymath.noteapi.util.ApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin
public class NotesController {
    @Autowired
    private NotesRepo notesRepo;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private JWTService jwtService;

//    @GetMapping("")
//    public List<Notes> getNotes() {
//        return notesRepo.findAll();
//    }
    @GetMapping("")
    public ResponseEntity<?> getAllUserNotes(@RequestHeader("Authorization") String authHeader) {
        try {
           // System.out.println(authHeader);
            String token = authHeader.substring(7);
            UUID userId = UUID.fromString(jwtService.extractUserId(token));
            List<Notes> notes = notesRepo.findAllByUserId(userId);
            if (notes.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(notes);


        }catch (MalformedJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiError(HttpStatus.UNAUTHORIZED,e.getMessage(),null));
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiError(HttpStatus.BAD_REQUEST, "Invalid Authorization header format", null));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null));
        }

    }
    @PostMapping("/save")
    public ResponseEntity<?> saveNote(@RequestBody Notes note) {
        try {
            UUID userId = note.getUserId();
            Users user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));
            note.setUserId(userId);
            note.setLastModified(LocalDateTime.now());
            note.setAuthor(user.getFirstName() + " " + user.getLastName());
             notesRepo.save(note);
             return ResponseEntity.ok(note);
        }catch (MalformedJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiError(HttpStatus.UNAUTHORIZED,e.getMessage(),null));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null));
        }
    }


    @GetMapping("/{id}")
    public Optional<Notes> getNotesById(@PathVariable UUID id) {
        return notesRepo.findAllById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchAllByTitleOrContentOrTag(@RequestParam(required = false) String title, @RequestParam(required = false) String content, @RequestParam(required = false) List<String> tags,@RequestHeader("Authorization") String authHeader) {

       try {
           String token = authHeader.substring(7);
           UUID userId = UUID.fromString(jwtService.extractUserId(token));

           if ((title == null || title.isEmpty()) &&
                   (content == null || content.isEmpty()) &&
                   (tags == null || tags.isEmpty())) {
               return ResponseEntity.badRequest().body(new ApiError(HttpStatus.BAD_REQUEST, "At least one search parameter must be provided", null));
           }

           if (tags == null) {
               tags = Collections.emptyList();
           }
//           List<Notes> notesList = notesRepo.findAllByUserId(userId);
           System.out.println(userId);
           List<Notes> notes = notesRepo.findNotesByTitleOrContentOrTags(title,content,tags,userId);
          if (notes.isEmpty()) {
              return ResponseEntity.ok(Collections.emptyList());
          }
          return ResponseEntity.ok(notes);
       }catch (MalformedJwtException e) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiError(HttpStatus.UNAUTHORIZED,e.getMessage(),null));
       }
       catch (Exception e) {
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
            existingNote.setLastModified(LocalDateTime.now());
//            existingNote.setAuthor(note.;
            notesRepo.save(existingNote);
            return ResponseEntity.ok(existingNote);
        }catch (MalformedJwtException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiError(HttpStatus.UNAUTHORIZED,e.getMessage(),null));
        } catch (Exception e) {
           return ResponseEntity.badRequest().body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage(),null));
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable UUID id) {
        try {
            Notes notes = notesRepo.findNoteById(id);
            if(notes == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(HttpStatus.NOT_FOUND,"Note not found",null));
            }
           notesRepo.deleteById(id);
           return ResponseEntity.status(HttpStatus.OK).body(notes);
        }catch (MalformedJwtException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiError(HttpStatus.UNAUTHORIZED,e.getMessage(),null));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage(),null));
        }
    }
}
