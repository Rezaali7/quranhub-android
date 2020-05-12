package app.quranhub.mushaf.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;
import app.quranhub.mushaf.data.entity.AyaRecorder;
import app.quranhub.mushaf.data.entity.QuranAudio;


@Dao
public interface QuranAudioDao {

    /* Just a test */
    //TODO remove this
    @Query("SELECT * FROM QURANAUDIO;")
    List<QuranAudio> getAll();

    @Query("SELECT file_path FROM QURANAUDIO where aya_id=:id")
    Single<String> getAllAyaAudioPathTest(int id);

    @Query("SELECT * FROM QuranAudio where id IN (:quranAudioIds) ORDER BY aya_id")
    List<QuranAudio> getAllByIds(int[] quranAudioIds);

    @Query("SELECT * FROM QuranAudio Where id=:quranAudioId")
    QuranAudio getById(int quranAudioId);

    @Query("SELECT * FROM QuranAudio as q JOIN SheikhRecitation as sr " +
            "ON q.sheikh_recitation_id=sr.id WHERE recitation_id=:recitationId " +
            "AND sheikh_id=:reciterId AND sura=:suraId")
    List<QuranAudio> getForSura(int recitationId, String reciterId, int suraId);

    @Insert
    void insert(QuranAudio quranAudio);

    @Insert
    void insertAll(QuranAudio[] quranAudios);

    @Delete
    void delete(QuranAudio quranAudio);

    @Delete
    void deleteAll(QuranAudio[] quranAudios);

    @Query("DELETE FROM QuranAudio WHERE (sheikh_recitation_id = " +
            "(SELECT sheikh_recitation_id FROM QuranAudio as q JOIN SheikhRecitation as sr " +
            "ON q.sheikh_recitation_id=sr.id WHERE recitation_id=:recitationId AND " +
            "sheikh_id=:reciterId) AND sura=:suraId)")
    void deleteForSura(int recitationId, String reciterId, int suraId);

    @Query("SELECT QuranAudio.file_path from QuranAudio join SheikhRecitation " +
            "on QuranAudio.sheikh_recitation_id=SheikhRecitation.id " +
            "and QuranAudio.aya_id=:ayaId " +
            "and SheikhRecitation.recitation_id=:recitation " +
            "and SheikhRecitation.sheikh_id=:sheikh")
    Single<String> getAyaAudioPath(int ayaId, int recitation, String sheikh);

    @Query("select recorderPath from ayarecorder where ayaId=:ayaId and recitation=:recitation")
    Single<String> getAyaRecorderPath(int ayaId, int recitation);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAyaRecorder(AyaRecorder recorder);

    @Query("delete from AyaRecorder where ayaId=:ayaId and recitation=:recitation")
    void deleteAyaVoiceRecorder(int ayaId, int recitation);

}
