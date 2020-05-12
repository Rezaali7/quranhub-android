package app.quranhub.mushaf.view;

import java.util.List;

import app.quranhub.base.BaseView;
import app.quranhub.mushaf.model.SuraIndexModelMapper;

public interface SuraGuz2IndexView extends BaseView {

    void onGetIndex(List<SuraIndexModelMapper> indexList);
}
