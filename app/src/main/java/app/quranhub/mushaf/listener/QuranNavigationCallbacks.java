package app.quranhub.mushaf.listener;

public interface QuranNavigationCallbacks {

    void gotoQuranPage(int pageNumber);

    void gotoQuranPageAya(int pageNumber, int ayaId, boolean addToStack);

}
