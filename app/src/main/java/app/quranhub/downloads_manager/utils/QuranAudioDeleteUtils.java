package app.quranhub.downloads_manager.utils;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.List;

import app.quranhub.mushaf.data.db.UserDatabase;
import app.quranhub.mushaf.data.entity.QuranAudio;
import app.quranhub.mushaf.data.entity.Sheikh;
import app.quranhub.utils.PreferencesUtils;

public final class QuranAudioDeleteUtils {

    private QuranAudioDeleteUtils() { /* Prevent instantiation */ }

    public static void deleteRecitationAudio(@NonNull Context context, int recitationId
            , DeleteFinishListener deleteFinishListener) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                // 1. delete from file system recitation folder with all of its contents
                String recitationDirPath = QuranAudioFileUtils.getLocalDirPath(context, recitationId);
                if (recitationDirPath != null) {
                    File dir = new File(recitationDirPath);
                    if (dir.exists()) {
                        deleteRecursive(dir);
                    }
                }

                // 2. delete from DB
                UserDatabase userDatabase = UserDatabase.getInstance(context);
                List<Sheikh> reciters = userDatabase.getSheikhRecitationDao()
                        .getRecitersForRecitation(recitationId);
                userDatabase.getSheikhDao().deleteAll(reciters.toArray(new Sheikh[0]));

                // 3. delete reciter preference if same recitation
                int recitationIdPreference = PreferencesUtils.getRecitationSetting(context);
                if (recitationIdPreference == recitationId) {
                    PreferencesUtils.resetReciterSheikhSetting(context);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                deleteFinishListener.onDeleteFinish();
            }
        }.execute();

    }

    public static void deleteReciterAudio(@NonNull Context context, int recitationId
            , @NonNull String reciterId, DeleteFinishListener deleteFinishListener) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                // 1. delete from file system the reciter folder for this recitation with all of its contents
                String reciterDirPath = QuranAudioFileUtils.getLocalDirPath(context, recitationId
                        , reciterId);
                if (reciterDirPath != null) {
                    File dir = new File(reciterDirPath);
                    if (dir.exists()) {
                        deleteRecursive(dir);
                    }
                }

                // 2. delete from DB
                UserDatabase userDatabase = UserDatabase.getInstance(context);
                userDatabase.getSheikhRecitationDao().delete(recitationId, reciterId);
                // TODO delete also the reciter if he has no suras in any recitation

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                deleteFinishListener.onDeleteFinish();
            }
        }.execute();
    }

    public static void deleteSuraAudio(@NonNull Context context, int recitationId
            , @NonNull String reciterId, int suraId, DeleteFinishListener deleteFinishListener) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                UserDatabase userDatabase = UserDatabase.getInstance(context);

                // 1. delete from file system the reciter folder for this recitation with all of its contents
                List<QuranAudio> quranAudios = userDatabase.getQuranAudioDao()
                        .getForSura(recitationId, reciterId, suraId);
                for (QuranAudio q : quranAudios) {
                    String audioFilePath = context.getExternalFilesDir(null).getPath() + q.getFilePath();
                    File audioFile = new File(audioFilePath);
                    if (audioFile.exists()) {
                        audioFile.delete();
                    }
                }

                // 2. delete from DB
                userDatabase.getQuranAudioDao().deleteForSura(recitationId
                        , reciterId, suraId);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                deleteFinishListener.onDeleteFinish();
            }
        }.execute();
    }

    private static void deleteRecursive(File dir) {

        if (dir.isDirectory())
            for (File child : dir.listFiles())
                deleteRecursive(child);

        dir.delete();
    }


    public interface DeleteFinishListener {
        void onDeleteFinish();
    }
}
