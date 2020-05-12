package app.quranhub.mushaf.interactor;

import java.util.List;

import app.quranhub.mushaf.model.SearchModel;

public interface TopicInteractor {


    void getAyas(int categoryId);

    interface TopicListener {
        void onGetTopics(List<SearchModel> searchModels);
    }
}
