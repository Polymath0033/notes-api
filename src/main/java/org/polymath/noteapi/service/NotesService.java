package org.polymath.noteapi.service;

import jakarta.transaction.Transactional;
import org.polymath.noteapi.dto.request.NoteRequest;
import org.polymath.noteapi.dto.response.NoteResponse;
import org.polymath.noteapi.exceptions.CustomBadRequest;
import org.polymath.noteapi.exceptions.CustomNotFound;
import org.polymath.noteapi.exceptions.UserDoesNotExist;
import org.polymath.noteapi.models.Notes;
import org.polymath.noteapi.models.Users;
import org.polymath.noteapi.repositories.NotesRepository;
import org.polymath.noteapi.repositories.UserRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class NotesService {
    private final NotesRepository notesRepo;
    private final UserRepo userRepo;


    public NotesService(NotesRepository notesRepo, UserRepo userRepo) {
        this.notesRepo = notesRepo;
        this.userRepo = userRepo;

    }

    public NoteResponse getNoteById(UUID id, String email){
        if(id==null){
            throw new CustomBadRequest("id is null");
        }
        if(email==null){
            throw new CustomBadRequest("Your token has expired or not set properly, please login again");
        }
        Notes note = notesRepo.findNotesByIdAndUserEmail(id,email).orElseThrow(()->new CustomNotFound("No note found"));
       return getNoteResponse(note);

    }
    public List<NoteResponse> getAllNotes(String email){
        if(email==null){
            throw new CustomBadRequest("Your token has expired or not set properly, please login again");
        }
        List<Notes> notes = notesRepo.findAllByUserEmail(email).orElseThrow(()->new CustomNotFound("No note found"));
        return getAllNotesResponse(notes);
    }
    public List<NoteResponse> getAllArchivedNotes(String email){
        if(email==null){
            throw new CustomBadRequest("Your token has expired or not set properly, please login again");
        }
        List<Notes> notes = notesRepo.findAllByUserEmailAndArchived(email).orElseThrow(()->new CustomNotFound("No archived note found"));
        return getAllNotesResponse(notes);
    }
    public List<NoteResponse> searchByTitle(String title,String email){
        if(title==null){
            throw new CustomBadRequest("Title is null");
        }
        if(email==null){
            throw new CustomBadRequest("Your token has expired or not set properly, please login again");
        }
        List<Notes> notes = notesRepo.findAllByUserEmailAndTitleContainsIgnoreCase(email,title).orElseThrow(()->new CustomNotFound("No note found"));
        return getAllNotesResponse(notes);
    }
    @Transactional
    public NoteResponse saveNote(String email, NoteRequest noteRequest){
        if(noteRequest.title()==null || noteRequest.title().isEmpty()){
            throw new CustomBadRequest("Title cannot be empty");
        }
        Users user = userRepo.findUserByEmail(email).orElseThrow(()->new UserDoesNotExist("You need to be authenticated in order to save note"));
        Notes note = new Notes();
        note.setTitle(noteRequest.title());
        note.setContent(noteRequest.content());
        note.setUser(user);
        note.setModifiedAt(LocalDateTime.now());
        Set<String> tags = new HashSet<>(noteRequest.tags());
        note.setTag(tags);
        notesRepo.save(note);
        return getNoteResponse(note);

    }
    public List<NoteResponse> searchNoteByTitleOrContentOrTags(String email,String searchText){
        if(searchText==null || searchText.isEmpty()){
            throw new CustomBadRequest("Search text cannot be empty");
        }
        if(email==null){
            throw new CustomBadRequest("Your token has expired or not set properly, please login again");
        }
        System.out.println(searchText);
        System.out.println(notesRepo.findAllBySearchTitleOrContentOrTag(searchText,email));
        List<Notes> notes = notesRepo.findAllBySearchTitleOrContentOrTag(searchText,email).orElseThrow(()->new CustomNotFound("No note found"));
        return getAllNotesResponse(notes);
    }
    public NoteResponse updateNote(String email,UUID id, NoteRequest noteRequest){
        if(id==null){
            throw new CustomBadRequest("id is null");
        }
        if(email==null){
            throw new CustomBadRequest("Your token has expired or not set properly, please login again");
        }
        if(noteRequest.title()==null||noteRequest.title().isEmpty()){
            throw new CustomBadRequest("Note title cannot be empty");
        }
        Notes existingNote = notesRepo.findNotesByIdAndUserEmail(id,email).orElseThrow(()->new CustomNotFound("No note found"));
        existingNote.setTitle(noteRequest.title());
        existingNote.setContent(noteRequest.content());
        existingNote.setModifiedAt(LocalDateTime.now());
        Set<String> tags = new HashSet<>(noteRequest.tags());
        existingNote.setTag(tags);
        notesRepo.save(existingNote);
        return null;
    }
    public void deleteNoteById(UUID id,String email){
        if(id==null){
            throw new CustomBadRequest("id is null");
        }
        if(email==null){
            throw new CustomBadRequest("Your token has expired or not set properly, please login again");
        }
        Notes notes = notesRepo.findNotesByIdAndUserEmail(id,email).orElseThrow(()->new CustomNotFound("No note found"));
        notesRepo.delete(notes);

    }
    public NoteResponse archiveNote(UUID id,String email){
        if(id==null){
            throw new CustomBadRequest("id is null");
        }
        if(email==null){
            throw new CustomBadRequest("Your token has expired or not set properly, please login again");
        }
        Notes notes = notesRepo.findNotesByIdAndUserEmail(id,email).orElseThrow(()->new CustomNotFound("No note found"));
        notes.setArchived(!notes.isArchived());
        notesRepo.save(notes);
        return getNoteResponse(notes);
    }

    private NoteResponse getNoteResponse(Notes note){
        return new NoteResponse(note.getId(),note.getTitle(),note.getContent(),note.getModifiedAt(),note.getTag(),note.isArchived());
    }
    private List<NoteResponse> getAllNotesResponse(List<Notes> notes){
        List<NoteResponse> responses = new ArrayList<>();
        for(Notes note : notes){
            responses.add(getNoteResponse(note));
        }
        return responses;
    }

}
