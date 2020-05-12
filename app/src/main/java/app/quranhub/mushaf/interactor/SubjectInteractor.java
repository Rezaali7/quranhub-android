package app.quranhub.mushaf.interactor;

import java.util.List;

import app.quranhub.mushaf.model.TopicModel;

public interface SubjectInteractor {

    void getSubjects(List<String> subjects, List<String> subjectsCategory);


    interface SubjectListener {
        void onGetSubjects(List<TopicModel> topicModels);
    }
}
