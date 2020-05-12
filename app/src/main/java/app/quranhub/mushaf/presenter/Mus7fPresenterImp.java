package app.quranhub.mushaf.presenter;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import app.quranhub.mushaf.data.entity.Aya;
import app.quranhub.mushaf.model.QuranPageInfo;
import app.quranhub.mushaf.model.SuraVersesNumber;
import app.quranhub.mushaf.view.MushfView;
import app.quranhub.Constants;
import app.quranhub.R;
import app.quranhub.base.BasePresenterImp;
import app.quranhub.mushaf.data.entity.TranslationBook;
import app.quranhub.mushaf.interactor.Mus7fInteractor;
import app.quranhub.mushaf.interactor.Mus7fInteractorImp;
import app.quranhub.utils.PreferencesUtils;

public class Mus7fPresenterImp extends BasePresenterImp<MushfView>
        implements Mus7fPresenter<MushfView>, Mus7fInteractor.ResultListener {

    private static final String TAG = Mus7fPresenterImp.class.getSimpleName();

    @NonNull
    private Mus7fInteractor interactor;
    @NonNull
    private Context context;

    public Mus7fPresenterImp(@NonNull Context context) {
        this.context = context;
        this.interactor = new Mus7fInteractorImp(this, context);
    }

    @Override
    public void getQuranPageInfo(int currentPage) {
        currentPage = Constants.QURAN.NUM_OF_PAGES - currentPage;
        interactor.getPageInfo(currentPage);
    }

    @Override
    public void setNightMode(boolean nightMode) {
        PreferencesUtils.persistNightModeSetting(context, nightMode);
    }

    @Override
    public boolean getNightMode() {
        return PreferencesUtils.getNightModeSetting(context);
    }

    @Override
    public boolean toggleNightMode() {
        boolean nightMode = !getNightMode();
        setNightMode(nightMode);
        return nightMode;
    }



    @Override
    public void getAyaTafseer(int ayaId) {
        interactor.getAyaTafseer(ayaId);
    }

    @Override
    public void getCurrentTafseerBook(String currentTafsserId) {
        interactor.getTafseerBook(currentTafsserId);
    }

    @Override
    public void onGetPageInfo(QuranPageInfo pageInfo) {
        if (isViewAttached()) {
            baseView.showQuranPageInfo(pageInfo);
        }
    }

    @Override
    public void onGetAyaTafseer(String tafseer) {
        if(isViewAttached()) {
            baseView.onGetAyaTafseer(tafseer);
        }
    }

    @Override
    public void onGetTafsserBook(TranslationBook book) {
        if(isViewAttached()) {
            interactor.initTranslationDB(book.getDatabaseName());
            baseView.onGetTafseerBook(book);
        }
    }

    @Override
    public void getSurasInPage() {
        interactor.getPageSuras();
    }


    @Override
    public void onGetSuraPage(ArrayList<ArrayList<Integer>> suras) {
        if(isViewAttached()) {
           baseView.onGetPageSuras(suras);
        }
    }

    @Override
    public void onErroOccured() {
        if (isViewAttached()) {
            baseView.showMessage(context.getString(R.string.page_info_failed));
        }
    }

    @Override
    public void checkAyaHasRecorder(int id) {
        interactor.checkAyaHasRecorder(id);
    }

    @Override
    public void saveRecorderPath(int ayaId, String recorderPath) {
        if(isViewAttached()) {
            interactor.saveRecorderPath(ayaId, recorderPath);
        }
    }

    @Override
    public void deleteAyaVoiceRecorder(int ayaId) {
        if(isViewAttached()) {
            interactor.deleteAyaVoiceRecorder(ayaId);
        }
    }

    @Override
    public void getSuraNumofVerses() {
        if(isViewAttached()) {
            interactor.getSuraNumofVerses();
        }
    }

    @Override
    public void getFromAyaPage(int fromAya) {
        if(isViewAttached()) {
            interactor.getFromAyaPage(fromAya);
        }
    }

    @Override
    public void getNotificationAya(int ayaId) {
        if(isViewAttached()) {
            interactor.getAya(ayaId);
        }
    }

    @Override
    public void onNoBooks() {
        baseView.onNoBooksExist();
    }

    @Override
    public void onAyaHasRecorder(String recorderPath) {
        if(isViewAttached()) {
            baseView.onGetAyaRecorder(recorderPath);
        }
    }

    @Override
    public void onGetSuraVersesNumber(ArrayList<SuraVersesNumber> suraVersesNumbers) {
        if(isViewAttached()) {
            baseView.onGetSuraVersesNumber(suraVersesNumbers);
        }
    }

    @Override
    public void onGetAyaPage(int page) {
        if(isViewAttached()) {
            baseView.onGetAyaPage(page);
        }
    }

    @Override
    public void onGetAya(Aya aya) {
        if(isViewAttached()) {
            baseView.onGetCurrentAyaFromNotification(aya);
        }
    }


}
