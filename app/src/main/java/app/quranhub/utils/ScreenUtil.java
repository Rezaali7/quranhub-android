package app.quranhub.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import java.util.Arrays;

import app.quranhub.mushaf.model.ScreenSize;

public class ScreenUtil {

    private static final String TAG = ScreenUtil.class.getSimpleName();

    public static final String PORTRAIT_STATE = "PORTRAIT";
    public static final String LANDSCAPE_STATE = "LANDSCAPE";

    public static String getOrientationState(Context context) {

        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return PORTRAIT_STATE;
        } else {
            return LANDSCAPE_STATE;
        }
    }

    public static boolean isPortrait(Context context) {
        if (context != null && getOrientationState(context).equals(PORTRAIT_STATE))
            return true;
        return false;
    }

    public static boolean isLandscape(Context context) {
        if (context != null && getOrientationState(context).equals(LANDSCAPE_STATE))
            return true;
        return false;
    }

    public static int getStatusBarHeight(Context context, ImageView quranPageIv) {
        if (context == null)
            return 0;

        int[] coordOffset = new int[2];
        quranPageIv.getLocationOnScreen(coordOffset);
        Log.d(TAG, "coordOffset = " + Arrays.toString(coordOffset));
        int statusBarHeight = (int) Math.ceil(25 * context.getResources().getDisplayMetrics().density);
        return coordOffset[1] - statusBarHeight;
    }

    public static void dismissKeyboard(@NonNull Context context, @NonNull View view) {
        if (context != null && view != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Gets the available screen width & height. Ignores the bottom system navigation bar (if exists).
     * Example: In PORTRAIT width:720, height:1184. In LANDSCAPE width:1184, height: 720.
     *
     * @param activity current activity reference.
     * @return ScreenSize instance holding width & height information.
     */
    public static ScreenSize getScreenSize(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        ScreenSize screenSize = new ScreenSize(size.x, size.y);
        Log.d(TAG, "getScreenSize(): width = " + screenSize.getWidth()
                + ", height = " + screenSize.getHeight());
        return screenSize;
    }

    /**
     * Keep the device's screen turned on and bright.
     *
     * @param activity
     * @param enable   whether to enable or disable this feature
     */
    public static void keepScreenOn(@NonNull Activity activity, boolean enable) {
        if (enable) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public static boolean isLayoutRtl(View view) {
        return ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

}
