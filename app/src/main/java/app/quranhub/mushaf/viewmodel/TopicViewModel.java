package app.quranhub.mushaf.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import app.quranhub.mushaf.interactor.TopicInteractor;
import app.quranhub.mushaf.interactor.TopicInteractorImp;
import app.quranhub.mushaf.model.SearchModel;

public class TopicViewModel extends AndroidViewModel implements TopicInteractor.TopicListener {

    private MediatorLiveData<List<SearchModel>> ayahs;
    private TopicInteractor interactor;

    public TopicViewModel(@NonNull Application application) {
        super(application);
        ayahs = new MediatorLiveData<>();
        interactor = new TopicInteractorImp(application, this);
    }

    public MediatorLiveData<List<SearchModel>> getAyahs() {
        return ayahs;
    }

    public void getAyas(int categoryId) {
        interactor.getAyas(categoryId);
    }

    @Override
    public void onGetTopics(List<SearchModel> searchModels) {
        ayahs.setValue(searchModels);
    }
}
