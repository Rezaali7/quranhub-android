package app.quranhub.mushaf.interactor;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import app.quranhub.mushaf.data.db.MushafDatabase;
import app.quranhub.mushaf.data.db.TranslationDatabase;
import app.quranhub.mushaf.data.entity.Translation;
import app.quranhub.mushaf.model.TafseerModel;

public class TafseerInteractorImp implements TafseerInteractor {

    private Context context;
    @NonNull
    private MushafDatabase mushafDatabase;
    private TranslationDatabase translationDatabase;


    public TafseerInteractorImp(@NonNull Context context) {
        this.context = context;
        mushafDatabase = MushafDatabase.getInstance(context.getApplicationContext());
    }

    @Override
    public void initTranslationDB(String dbName) {
        if (translationDatabase != null) {
            translationDatabase.close();
        }
        translationDatabase = TranslationDatabase.getInstance(context, dbName);
    }

    @Override
    public LiveData<List<Translation>> getSuraBookTafseers(int suraNumber) {
        return translationDatabase.getTranslationDao().getAyasTafseer(suraNumber);
    }

    @Override
    public LiveData<List<TafseerModel>> getSuraAyah(int suraNumber) {
        return mushafDatabase.getAyaDao().getPageTafseers(suraNumber);
    }


    @Override
    public LiveData<List<TafseerModel>> getSuraTafseers(int suraNumber) {

        return mushafDatabase.getAyaDao().getPageTafseers(suraNumber);
    }

}
