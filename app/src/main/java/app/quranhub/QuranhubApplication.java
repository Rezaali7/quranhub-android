package app.quranhub;

import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import com.downloader.PRDownloader;

import app.quranhub.utils.LocaleUtil;

public class QuranhubApplication extends MultiDexApplication {

    private static final String TAG = QuranhubApplication.class.getSimpleName();

    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();

        LocaleUtil.initAppLanguage(this);

        // initialize PRDownloader library (for downloading files)
        PRDownloader.initialize(getApplicationContext());
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Android resets the locale for the top level resources back to the device default
        // on every application restart and configuration change.
        LocaleUtil.initAppLanguage(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtil.initAppLanguage(base));
    }
}
