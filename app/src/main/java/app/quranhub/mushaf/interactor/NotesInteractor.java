package app.quranhub.mushaf.interactor;

import androidx.lifecycle.LiveData;

import java.util.List;

import app.quranhub.mushaf.data.entity.Note;
import app.quranhub.mushaf.model.DisplayedNote;

public interface NotesInteractor {

    LiveData<List<DisplayedNote>> getNotes();

    void editNote(Note note);

    void deleteNote(int ayaId);

}
