package app.quranhub.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import app.quranhub.R;
import app.quranhub.base.BaseActivity;
import app.quranhub.first_wizard.FirstTimeWizardActivity;
import app.quranhub.main.MainActivity;
import app.quranhub.mushaf.data.db.MushafDatabase;
import app.quranhub.mushaf.data.db.RoomAsset;
import app.quranhub.utils.PreferencesUtils;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private static final int SPLASH_DURATION = 1500;  // 1.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Log.d(TAG, "Opening the next activity after splash screen.");

            if (PreferencesUtils.isFirstTimeWizardDone(this)) {
                // open MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                // open FirstTimeWizardActivity
                Intent intent = new Intent(this, FirstTimeWizardActivity.class);
                startActivity(intent);
            }

            finish();
        }, SPLASH_DURATION);

        // initialize Mus'haf metadata DB in the splash screen wasted time
        RoomAsset.initializeDatabase(this, MushafDatabase.DATABASE_NAME
                , MushafDatabase.ASSET_DB_VERSION);
    }

}
