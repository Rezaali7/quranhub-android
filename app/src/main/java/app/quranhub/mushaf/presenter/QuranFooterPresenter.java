package app.quranhub.mushaf.presenter;

import app.quranhub.mushaf.view.QuranFooterView;
import app.quranhub.base.BasePresenter;

public interface QuranFooterPresenter extends BasePresenter<QuranFooterView> {

    void displaySearchDialog();

    void toggleNightMode();

}
