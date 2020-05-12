package app.quranhub.mushaf.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import app.quranhub.mushaf.data.entity.Note;
import app.quranhub.mushaf.model.DisplayedNote;
import app.quranhub.mushaf.interactor.NotesInteractor;
import app.quranhub.mushaf.interactor.NotesInteractorImp;

public class NotesViewModel extends AndroidViewModel {

    private NotesInteractor interactor;
    private LiveData<List<DisplayedNote>> notesLiveData;
    private MediatorLiveData<List<DisplayedNote>> notes;

    public NotesViewModel(@NonNull Application application) {
        super(application);
        interactor = new NotesInteractorImp(application);
        notes = new MediatorLiveData<>();
    }

    public void getAllNotes() {
        notesLiveData = interactor.getNotes();
        notes.addSource(notesLiveData, displayedNotes -> {
            notes.setValue(displayedNotes);
            notes.removeSource(notesLiveData);
        });
    }

    public MediatorLiveData<List<DisplayedNote>> getNotes() {
        return notes;
    }

    public void updateNote(Note note) {
        interactor.editNote(note);
    }

    public void deleteNote(int ayaId) {
        interactor.deleteNote(ayaId);
    }

}
