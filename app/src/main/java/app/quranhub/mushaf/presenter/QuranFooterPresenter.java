package app.quranhub.mushaf.presenter;

import app.quranhub.base.BasePresenter;
import app.quranhub.mushaf.view.QuranFooterView;

public interface QuranFooterPresenter extends BasePresenter<QuranFooterView> {

    void displaySearchDialog();

    void toggleNightMode();

}
