package app.quranhub.mushaf.presenter;

import app.quranhub.mushaf.view.QuranFooterView;
import app.quranhub.base.BasePresenterImp;

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
