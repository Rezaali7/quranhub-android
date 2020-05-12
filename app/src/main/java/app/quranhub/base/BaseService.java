package app.quranhub.base;

import android.app.Service;
import android.content.Context;

import app.quranhub.utils.LocaleUtil;

public abstract class BaseService extends Service {

    @Override
    public void onCreate() {
        LocaleUtil.initAppLanguage(this);
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleUtil.initAppLanguage(newBase));
    }


}
