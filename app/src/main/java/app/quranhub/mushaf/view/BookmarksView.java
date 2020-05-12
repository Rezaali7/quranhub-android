package app.quranhub.mushaf.view;

import androidx.annotation.NonNull;

import app.quranhub.base.BaseView;

public interface BookmarksView extends BaseView {

    void enableEditList();

    void finishEditList();

    void filterList();

    void searchList(@NonNull String text);

}
