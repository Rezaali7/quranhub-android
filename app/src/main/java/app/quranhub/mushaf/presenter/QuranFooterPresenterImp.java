package app.quranhub.mushaf.presenter;

import app.quranhub.base.BasePresenterImp;
import app.quranhub.mushaf.view.QuranFooterView;

public class QuranFooterPresenterImp extends BasePresenterImp<QuranFooterView>
        implements QuranFooterPresenter {

    @Override
    public void displaySearchDialog() {
        baseView.displaySearchDialog();
    }

    @Override
    public void toggleNightMode() {
        baseView.toggleNightMode();
    }

}
