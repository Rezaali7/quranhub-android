package app.quranhub.mushaf.interactor;

import androidx.lifecycle.LiveData;

import java.util.List;

import app.quranhub.mushaf.model.TafseerModel;
import app.quranhub.mushaf.data.entity.Translation;

public interface TafseerInteractor {

    LiveData<List<TafseerModel>> getSuraTafseers(int suraNumber);
    void initTranslationDB(String dbName);

    LiveData<List<Translation>> getSuraBookTafseers(int suraNumber);

    LiveData<List<TafseerModel>> getSuraAyah(int suraNumber);
}
