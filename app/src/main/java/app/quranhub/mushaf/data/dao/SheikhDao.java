package app.quranhub.mushaf.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import app.quranhub.mushaf.data.entity.Sheikh;

@Dao
public interface SheikhDao {

    @Query("SELECT * FROM Sheikh")
    List<Sheikh> getAll();

    @Query("SELECT * FROM Sheikh where id IN (:sheikhsIds)")
    List<Sheikh> getAllByIds(int[] sheikhsIds);

    @Query("SELECT s.id, s.ar_name FROM Sheikh as s JOIN SheikhRecitation as sr " +
            "ON s.id=sr.sheikh_id WHERE sr.recitation_id=:recitationId")
    List<Sheikh> getAllForRecitation(int recitationId);

    @Query("SELECT * FROM Sheikh Where id=:sheikhId")
    Sheikh getById(String sheikhId);

    @Insert
    void insert(Sheikh sheikh);

    @Insert
    void insertAll(Sheikh[] sheikhs);

    @Delete
    void delete(Sheikh sheikh);

    @Delete
    void deleteAll(Sheikh[] sheikhs);

}
