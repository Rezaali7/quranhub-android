package app.quranhub.mushaf.interactor;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import app.quranhub.mushaf.model.SuraIndexModel;
import app.quranhub.mushaf.model.SuraIndexModelMapper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import app.quranhub.R;
import app.quranhub.mushaf.data.db.MushafDatabase;


public class SuraGuz2IndexInteractorImp implements SuraGuz2IndexInteractor {

    private static final String TAG = SuraGuz2IndexInteractorImp.class.getSimpleName();

    private MushafDatabase mushafDatabase;

    private SuraGuz2IndexInteractor.GetIndexListener listener;
    private Context context;

    public SuraGuz2IndexInteractorImp(SuraGuz2IndexInteractor.GetIndexListener listener, Context context) {
        mushafDatabase = MushafDatabase.getInstance(context);
        this.listener = listener;
        this.context = context;
    }

    @SuppressLint("CheckResult")
    @Override
    public void getSuraIndex() {

        mushafDatabase.getSuraDao()
                .getSuraIndexInfo().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(suraIndexModels -> {
                    List<SuraIndexModelMapper> suraIndexModelMapperList = new ArrayList<>();
                    for (SuraIndexModel model : suraIndexModels) {
                        if (model.getType().equals("Medinan")) {
                            model.setType(context.getString(R.string.sura_madnya));
                        } else if (model.getType().equals("Meccan")) {
                            model.setType(context.getString(R.string.sura_makya));
                        }
                        suraIndexModelMapperList.add(SuraIndexModelMapper.mapToString(model, context));
                    }
                    return suraIndexModelMapperList;
                })
                .subscribe(result -> {
                    listener.onGetIndex(result);
                }, error -> {
                    listener.onGetIndexFailed(context.getString(R.string.sura_index_failed));
                });

    }
}
