package app.quranhub.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import app.quranhub.R;
import app.quranhub.base.BaseActivity;
import app.quranhub.first_wizard.FirstTimeWizardActivity;
import app.quranhub.main.MainActivity;
import app.quranhub.mushaf.data.db.MushafDatabase;
import app.quranhub.mushaf.data.db.RoomAsset;
import app.quranhub.utils.PreferencesUtils;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private static final int FIRST_SPLASH_DELAY = 1500;
    private static final int SECOND_SPLASH_DELAY = 1500;

    ConstraintLayout layout;
    Handler handler;
    boolean firstSplash = true;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (firstSplash) {
                Log.d(TAG, "Switching second splash...");
                layout.setVisibility(View.VISIBLE);
                firstSplash = false;
                handler.postDelayed(this, SECOND_SPLASH_DELAY);
            } else {
                Log.d(TAG, "Opening the next activity.");

                if (PreferencesUtils.isFirstTimeWizardDone(SplashActivity.this)) {
                    // open MainActivity
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    // open FirstTimeWizardActivity
                    Intent intent = new Intent(SplashActivity.this, FirstTimeWizardActivity.class);
                    startActivity(intent);
                }

                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler = new Handler();
        layout = findViewById(R.id.parent_layout);
        handler.postDelayed(runnable, FIRST_SPLASH_DELAY);

        // initialize Mus'haf metadata DB in the splash screen wasted time
        RoomAsset.initializeDatabase(this, MushafDatabase.DATABASE_NAME
                , MushafDatabase.ASSET_DB_VERSION);
    }

}
