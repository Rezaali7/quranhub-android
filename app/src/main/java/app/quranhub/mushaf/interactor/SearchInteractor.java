package app.quranhub.mushaf.interactor;

import androidx.lifecycle.LiveData;

import java.util.List;

import app.quranhub.mushaf.model.SearchModel;

public interface SearchInteractor {

    void searchAya(String inputQuery);

    void searchAyaInGuz(String inputQuery, int guzNumber);

    void searchAyaInSura(String inputQuery, int suraNumber);

    LiveData<List<Integer>> getSurasInChapter(int chapter);

    void searchWithSuraAndJuz(String inputSearch, int selectedSura, int selectedJuz);

    void searchWithSuraAndJuzAndHizb(String inputSearch, int selectedSura, int selectedJuz, int selectedHezb);

    void searchWithSuraAndJuzAndHizbQuarter(String inputSearch, int selectedSura, int selectedJuz, int selectedHezb, int selectedQuarter);


    interface TopicListener {
        void onGetTopics(List<SearchModel> searchModels);
    }
}
