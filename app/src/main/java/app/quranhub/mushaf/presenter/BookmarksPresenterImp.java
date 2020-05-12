package app.quranhub.mushaf.presenter;

import androidx.annotation.NonNull;

import app.quranhub.mushaf.view.BookmarksView;
import app.quranhub.base.BasePresenterImp;

public class BookmarksPresenterImp extends BasePresenterImp<BookmarksView>
        implements BookmarksPresenter<BookmarksView> {

    public BookmarksPresenterImp() {

    }

    @Override
    public void enableEditList() {
        baseView.enableEditList();
    }

    @Override
    public void finishEditList() {
        baseView.finishEditList();
    }

    @Override
    public void filterList() {
        baseView.filterList();
    }

    @Override
    public void searchList(@NonNull String text) {
        baseView.searchList(text);
    }
}
