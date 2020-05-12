package app.quranhub.mushaf.interactor;

import java.util.List;

import app.quranhub.mushaf.model.SuraIndexModelMapper;

public interface SuraGuz2IndexInteractor {

    void getSuraIndex();

    public interface GetIndexListener {
        void onGetIndex(List<SuraIndexModelMapper> indexList);

        void onGetIndexFailed(String msg);
    }
}
