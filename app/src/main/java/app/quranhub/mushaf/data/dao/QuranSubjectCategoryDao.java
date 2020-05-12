package app.quranhub.mushaf.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;
import app.quranhub.mushaf.data.entity.QuranSubjectCategory;

@Dao
public interface QuranSubjectCategoryDao {

    @Query("SELECT * FROM QuranSubjectCategory")
    Single<List<QuranSubjectCategory>> getAll();

    @Query("SELECT * FROM QuranSubjectCategory WHERE id IN(:ids)")
    List<QuranSubjectCategory> getAllByIds(int... ids);


}
