package app.quranhub.mushaf.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import app.quranhub.mushaf.interactor.SubjectInteractor;
import app.quranhub.mushaf.interactor.SubjectInteractorImp;
import app.quranhub.mushaf.model.TopicModel;

public class SubjectsViewModel extends AndroidViewModel implements SubjectInteractor.SubjectListener {

    private MediatorLiveData<List<TopicModel>> subjectsLiveData;
    private SubjectInteractor interactor;


    public SubjectsViewModel(@NonNull Application application) {
        super(application);
        subjectsLiveData = new MediatorLiveData<>();
        interactor = new SubjectInteractorImp(application, this);
    }

    public void getSubjects(List<String> subjects, List<String> subjectsCategory) {
        interactor.getSubjects(subjects, subjectsCategory);
    }

    @Override
    public void onGetSubjects(List<TopicModel> topicModels) {
        subjectsLiveData.setValue(topicModels);
    }

    public MediatorLiveData<List<TopicModel>> getSubjectsLiveData() {
        return subjectsLiveData;
    }
}
