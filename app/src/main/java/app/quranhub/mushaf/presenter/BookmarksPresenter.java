package app.quranhub.mushaf.presenter;

import androidx.annotation.NonNull;

import app.quranhub.base.BasePresenter;
import app.quranhub.base.BaseView;

public interface BookmarksPresenter<T extends BaseView> extends BasePresenter<T> {

    void enableEditList();

    void finishEditList();

    void filterList();

    void searchList(@NonNull String text);

}
