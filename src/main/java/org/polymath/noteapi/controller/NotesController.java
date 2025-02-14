package org.polymath.noteapi.controller;

import org.polymath.noteapi.dto.request.NoteRequest;
import org.polymath.noteapi.dto.response.NoteResponse;
import org.polymath.noteapi.service.JWTService;
import org.polymath.noteapi.service.NotesService;
import org.polymath.noteapi.util.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notes")
public class NotesController {
    private final JWTService jwtService;
    private final NotesService notesService;

    public NotesController(JWTService jwtService, NotesService notesService) {
        this.jwtService = jwtService;
        this.notesService = notesService;
    }
    @PostMapping("")
    public ResponseEntity<?> createNote(@RequestBody NoteRequest noteRequest, @RequestHeader("Authorization") String authHeader) {
       NoteResponse response =  notesService.saveNote(getEmailFromAuthHeader(authHeader), noteRequest);
        return ResponseHandler.handleResponse(response, HttpStatus.CREATED,"New note created");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable("id") UUID id, @RequestBody NoteRequest noteRequest, @RequestHeader("Authorization") String authHeader) {
        NoteResponse response = notesService.updateNote(getEmailFromAuthHeader(authHeader), id, noteRequest);
        return ResponseHandler.handleResponse(response, HttpStatus.OK,"Note updated");
    }

    @PutMapping("/archive/{id}")
    public ResponseEntity<?> archiveNote(@PathVariable("id") UUID id, @RequestHeader("Authorization") String authHeader) {
        NoteResponse response = notesService.archiveNote(id, getEmailFromAuthHeader(authHeader));
        return ResponseHandler.handleResponse(response, HttpStatus.OK,"Note archived");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable("id") UUID id, @RequestHeader("Authorization") String authHeader) {
        notesService.deleteNoteById(id, getEmailFromAuthHeader(authHeader));
        return ResponseHandler.handleResponse(null, HttpStatus.OK,"Note deleted");
    }

    @GetMapping("")
    public ResponseEntity<?> getAllNotes(@RequestHeader("Authorization") String authHeader) {
        List<NoteResponse> response = notesService.getAllNotes(getEmailFromAuthHeader(authHeader));
        return ResponseHandler.handleResponse(response, HttpStatus.OK,"All notes retrieved");
    }
    @GetMapping("/archive")
    public ResponseEntity<?> getAllArchiveNotes(@RequestHeader("Authorization") String authHeader) {
        List<NoteResponse> response = notesService.getAllArchivedNotes(getEmailFromAuthHeader(authHeader));
        return ResponseHandler.handleResponse(response, HttpStatus.OK,"All archived notes retrieved");
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable("id") UUID id, @RequestHeader("Authorization") String authHeader) {
        NoteResponse response = notesService.getNoteById(id,getEmailFromAuthHeader(authHeader));
        return ResponseHandler.handleResponse(response, HttpStatus.OK,"Note retrieved");

    }

    @GetMapping("/title")
    public ResponseEntity<?> getAllNotesByTitle(@RequestParam(name = "title") String title, @RequestHeader("Authorization") String authHeader) {
        List<NoteResponse> responses = notesService.searchByTitle(title, getEmailFromAuthHeader(authHeader));
        return ResponseHandler.handleResponse(responses, HttpStatus.OK,"All notes with title: "+title+" retrieved");
    }

    @GetMapping("/search")
    public ResponseEntity<?> getAllByTitleOrContentOrTags(@RequestParam(name = "search") String search, @RequestHeader("Authorization") String authHeader) {
        List<NoteResponse> responses = notesService.searchNoteByTitleOrContentOrTags(getEmailFromAuthHeader(authHeader),search);
        return ResponseHandler.handleResponse(responses, HttpStatus.OK,"All notes containing this search keyword: "+search+" retrieved");
    }


    private String getEmailFromAuthHeader(String authHeader) {
        String token = authHeader.substring(7);
        return jwtService.extractEmail(token);
    }
}