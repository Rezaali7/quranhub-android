package app.quranhub.mushaf.interactor;

import androidx.lifecycle.LiveData;

import java.util.List;

import app.quranhub.mushaf.model.DisplayableBookmark;
import app.quranhub.mushaf.data.entity.AyaBookmark;
import app.quranhub.mushaf.data.entity.BookmarkType;
import app.quranhub.mushaf.data.entity.Sura;

public interface BookmarksInteractor {

    Sura getSura(int suraId);

    LiveData<List<DisplayableBookmark>> getAllBookmarks();

    void deleteBookmark(int bookmarkId);

    LiveData<List<AyaBookmark>> filterBookmarks(int filterType);

    void changeBookmarkType(int bookmarkId, int bookmarkTypeId);

    LiveData<List<BookmarkType>> getBookmarkTypes();
}
