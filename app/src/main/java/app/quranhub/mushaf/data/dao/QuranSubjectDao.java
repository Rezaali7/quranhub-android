package app.quranhub.mushaf.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;
import app.quranhub.mushaf.data.entity.QuranSubject;

@Dao
public interface QuranSubjectDao {

    @Query("SELECT * FROM QuranSubject")
    Single<List<QuranSubject>> getAll();

    @Query("SELECT * FROM QuranSubject WHERE id IN(:ids)")
    List<QuranSubject> getAllByIds(int... ids);


}
