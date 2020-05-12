package app.quranhub.mushaf.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import app.quranhub.mushaf.data.dao.TranslationDao;
import app.quranhub.mushaf.data.entity.Translation;

@Database(entities = {Translation.class}, version = 2, exportSchema = false)
public abstract class TranslationDatabase extends RoomDatabase {

    public static TranslationDatabase getInstance(@NonNull Context context, @NonNull String databaseName) {
        return Room.databaseBuilder(context.getApplicationContext(),
                TranslationDatabase.class, databaseName)
                .addMigrations(MIGRATION_1_2).build();
    }

    public abstract TranslationDao getTranslationDao();


    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            /* prevent creation of schema */
        }
    };

}
