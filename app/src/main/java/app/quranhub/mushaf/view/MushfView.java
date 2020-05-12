package app.quranhub.mushaf.view;

import java.util.ArrayList;

import app.quranhub.base.BaseView;
import app.quranhub.mushaf.data.entity.Aya;
import app.quranhub.mushaf.data.entity.TranslationBook;
import app.quranhub.mushaf.model.QuranPageInfo;
import app.quranhub.mushaf.model.SuraVersesNumber;

public interface MushfView extends BaseView {

    void showQuranPageInfo(QuranPageInfo quranPageInfo);

    void onGetAyaTafseer(String tafseer);

    void onGetTafseerBook(TranslationBook book);

    void onNoBooksExist();

    void onGetPageSuras(ArrayList<ArrayList<Integer>> suras);

    void onGetAyaRecorder(String recorderPath);

    void onGetSuraVersesNumber(ArrayList<SuraVersesNumber> suraVersesNumbers);

    void onGetAyaPage(int page);

    void onGetCurrentAyaFromNotification(Aya aya);

}
