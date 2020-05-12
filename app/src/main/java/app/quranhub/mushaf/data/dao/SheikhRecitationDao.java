package app.quranhub.mushaf.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import app.quranhub.mushaf.data.entity.Sheikh;
import app.quranhub.mushaf.data.entity.SheikhRecitation;

@Dao
public interface SheikhRecitationDao {

    @Query("SELECT * FROM SheikhRecitation")
    List<SheikhRecitation> getAll();

    @Query("SELECT * FROM SheikhRecitation where id IN (:sheikhRecitationsIds)")
    List<SheikhRecitation> getAllByIds(int[] sheikhRecitationsIds);

    @Query("SELECT * FROM SheikhRecitation Where id=:sheikhRecitationId")
    SheikhRecitation getById(int sheikhRecitationId);

    @Query("SELECT * FROM SheikhRecitation WHERE recitation_id=:recitationId AND sheikh_id=:reciterId")
    SheikhRecitation get(int recitationId, String reciterId);

    @Query("SELECT id FROM SheikhRecitation WHERE recitation_id=:recitationId AND sheikh_id=:sheikhId")
    int getSheikhRecitationId(int recitationId, String sheikhId);

    @Query("SELECT COUNT(DISTINCT sr.sheikh_id) FROM SheikhRecitation as sr JOIN QuranAudio as q " +
            "ON sr.id=q.sheikh_recitation_id WHERE sr.recitation_id=:recitationId")
    int getNumOfRecitersWithDownloads(int recitationId);

    @Query("SELECT s.* FROM Sheikh as s JOIN SheikhRecitation as sr ON s.id=sr.sheikh_id WHERE recitation_id=:recitationId")
    List<Sheikh> getRecitersForRecitation(int recitationId);

    @Query("SELECT DISTINCT sura FROM Sheikh as s JOIN SheikhRecitation as sr JOIN QuranAudio as q " +
            "ON s.id=sr.sheikh_id AND sr.id=q.sheikh_recitation_id WHERE sr.recitation_id=:recitationId " +
            "AND s.id=:reciterId")
    List<Integer> getSurasIdsForReciterInRecitation(int recitationId, String reciterId);

    @Insert
    void insert(SheikhRecitation sheikhRecitation);

    @Insert
    void insertAll(SheikhRecitation[] sheikhRecitations);

    @Delete
    void delete(SheikhRecitation sheikhRecitation);

    @Query("DELETE FROM sheikhrecitation WHERE recitation_id=:recitationId AND sheikh_id=:reciterId")
    void delete(int recitationId, String reciterId);

}
