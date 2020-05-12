package app.quranhub.mushaf.interactor;

import androidx.lifecycle.LiveData;

import java.util.List;

import app.quranhub.mushaf.model.TafseerModel;
import app.quranhub.mushaf.network.model.BookContent;
import app.quranhub.mushaf.data.entity.Book;

public interface BooksInteractor {

    void getAllTranslations();

    LiveData<List<Book>> getLocallyTranslations();
    LiveData<List<TafseerModel>> getSuraTafseers(int suraNumber);


    void updateTranslationDownload(int id, int type, long downloadId);

    void updateFinishedDownload(long downloadId, int type);


    interface TranslationsListener {
        void onError();
        void onGetAllTranslation(List<BookContent>  contents);
    }

}
