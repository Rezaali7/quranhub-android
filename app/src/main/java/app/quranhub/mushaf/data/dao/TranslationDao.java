package app.quranhub.mushaf.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;
import app.quranhub.mushaf.data.entity.Translation;

@Dao
public interface TranslationDao {

    @Query("SELECT * FROM translation")
    Single<List<Translation>> getAll();

    @Query("SELECT * FROM translation where `sura`=:suraNumber")
    LiveData<List<Translation>> getAyasTafseer(int suraNumber);

    @Query("SELECT text FROM translation WHERE `index`=:index")
    Single<String> findByIndex(int index);

    @Query("SELECT * FROM translation WHERE sura=:sura AND aya=:aya LIMIT 1")
    Translation findForAya(int sura, int aya);

}
