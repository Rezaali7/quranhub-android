package app.quranhub.mushaf.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import app.quranhub.Constants;
import app.quranhub.mushaf.data.dao.BookDao;
import app.quranhub.mushaf.data.dao.BookmarkDao;
import app.quranhub.mushaf.data.dao.NoteDao;
import app.quranhub.mushaf.data.dao.QuranAudioDao;
import app.quranhub.mushaf.data.dao.RecitationDao;
import app.quranhub.mushaf.data.dao.SheikhDao;
import app.quranhub.mushaf.data.dao.SheikhRecitationDao;
import app.quranhub.mushaf.data.dao.TranslationBookDao;
import app.quranhub.mushaf.data.entity.AyaBookmark;
import app.quranhub.mushaf.data.entity.AyaRecorder;
import app.quranhub.mushaf.data.entity.Book;
import app.quranhub.mushaf.data.entity.BookmarkType;
import app.quranhub.mushaf.data.entity.Note;
import app.quranhub.mushaf.data.entity.QuranAudio;
import app.quranhub.mushaf.data.entity.Recitation;
import app.quranhub.mushaf.data.entity.Sheikh;
import app.quranhub.mushaf.data.entity.SheikhRecitation;
import app.quranhub.mushaf.data.entity.TranslationBook;


@Database(entities = {AyaBookmark.class, BookmarkType.class, Book.class, TranslationBook.class,
        Note.class, Recitation.class, Sheikh.class, SheikhRecitation.class, QuranAudio.class, AyaRecorder.class}
        , version = 1, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "user.db";

    private static volatile UserDatabase instance;

    public static UserDatabase getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (UserDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            UserDatabase.class, DATABASE_NAME)
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    initBookmarkTypes(db);
                                    initAvailableRecitations(db);
                                }
                            })
                            .build();
                }
            }
        }
        return instance;
    }

    public abstract BookmarkDao getBookmarkDao();

    public abstract BookDao getBookDao();

    public abstract TranslationBookDao getTranslationBookDao();

    public abstract NoteDao getNoteDao();

    public abstract RecitationDao getRecitationDao();

    public abstract SheikhDao getSheikhDao();

    public abstract SheikhRecitationDao getSheikhRecitationDao();

    public abstract QuranAudioDao getQuranAudioDao();


    private static void initBookmarkTypes(@NonNull SupportSQLiteDatabase db) {

        String favoriteType = "INSERT INTO BookmarkType(typeId, bookmarkTypeName, colorIndex)\n" +
                "VALUES(1, \"Favorite\", 0)";
        String memorizeType = "INSERT INTO BookmarkType(typeId, bookmarkTypeName, colorIndex)\n" +
                "VALUES(2, \"Reciting\", 0)";
        String recitingType = "INSERT INTO BookmarkType(typeId, bookmarkTypeName, colorIndex)\n" +
                "VALUES(3, \"Note\", 0)";
        String noteType = "INSERT INTO BookmarkType(typeId, bookmarkTypeName, colorIndex)\n" +
                "VALUES(4, \"Memorize\", 0)";
        db.execSQL(favoriteType);
        db.execSQL(recitingType);
        db.execSQL(noteType);
        db.execSQL(memorizeType);

    }

    private static void initAvailableRecitations(@NonNull SupportSQLiteDatabase db) {
        db.execSQL("INSERT INTO Recitation VALUES (" + Constants.RECITATIONS.HAFS_ID + ", \"حفص عن عاصم\")");
        db.execSQL("INSERT INTO Recitation VALUES (" + Constants.RECITATIONS.WARSH_ID + ", \"ورش عن نافع\")");
    }

}
