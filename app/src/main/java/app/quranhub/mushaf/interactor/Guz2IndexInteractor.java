package app.quranhub.mushaf.interactor;

import androidx.lifecycle.LiveData;

import java.util.List;

import app.quranhub.mushaf.model.HizbQuarterDataModel;

public interface Guz2IndexInteractor {

    LiveData<List<HizbQuarterDataModel>> getAllHizbQuarterDataModel();

}
