package app.quranhub.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import app.quranhub.Constants;


public final class PreferencesUtils {


    private PreferencesUtils() { /* prevent instantiation */}

    private static final String PREF_NIGHT_MODE_SETTING = "PREF_NIGHT_MODE_SETTING";
    private static final String PREF_APP_LANG_SETTING = "PREF_APP_LANG_SETTING";
    private static final String PREF_SCREEN_READING_BACKLIGHT_SETTING = "PREF_SCREEN_READING_BACKLIGHT_SETTING";
    private static final String PREF_LAST_READ_PAGE_SETTING = "PREF_LAST_READ_PAGE";
    private static final String PREF_RECITATION_SETTING = "PREF_RECITATION_SETTING";
    private static final String PREF_FIRST_TIME_WIZARD_SHOW_FLAG = "PREF_FIRST_TIME_WIZARD_SHOW_FLAG";
    private static final String PREF_QURAN_TRANSLATION_LANGUAGE = "PREF_QURAN_TRANSLATION_LANGUAGE";
    private static final String PREF_QURAN_TRANSLATION_BOOK = "PREF_QURAN_TRANSLATION_BOOK";
    private static final String PREF_RECITER_SHEIKH_SETTING = "PREF_RECITER_SHEIKH_SETTING";

    public static boolean getNightModeSetting(@NonNull Context context) {
        return SharedPrefsUtil.getBoolean(context, PREF_NIGHT_MODE_SETTING, false);
    }

    public static void persistNightModeSetting(@NonNull Context context, boolean nightMode) {
        SharedPrefsUtil.saveBoolean(context, PREF_NIGHT_MODE_SETTING, nightMode);
    }

    @NonNull
    public static String getAppLangSetting(@NonNull Context context) {
        String defaultLangCode;
        if (Constants.SUPPORTED_LANGUAGES.CODES.contains(LocaleUtil.getAppLanguage())) {
            // System-defined app language is supported
            defaultLangCode = LocaleUtil.getAppLanguage();
        } else {
            defaultLangCode = Constants.SUPPORTED_LANGUAGES.DEFAULT_APP_LANGUAGE;
        }

        return SharedPrefsUtil.getString(context, PREF_APP_LANG_SETTING
                , defaultLangCode);
    }

    public static void persistAppLangSetting(@NonNull Context context, @NonNull String langCode) {
        SharedPrefsUtil.saveString(context, PREF_APP_LANG_SETTING, langCode);
        SharedPrefsUtil.clearPreference(context, PREF_QURAN_TRANSLATION_BOOK);
    }

    public static boolean getScreenReadingBacklightSetting(@NonNull Context context) {
        return SharedPrefsUtil.getBoolean(context, PREF_SCREEN_READING_BACKLIGHT_SETTING, true);
    }

    public static void persistScreenReadingBacklightSetting(@NonNull Context context, boolean enable) {
        SharedPrefsUtil.saveBoolean(context, PREF_SCREEN_READING_BACKLIGHT_SETTING, enable);
    }

    public static boolean getLastReadPageSetting(@NonNull Context context) {
        return SharedPrefsUtil.getBoolean(context, PREF_LAST_READ_PAGE_SETTING, true);
    }

    public static void persistLastReadPageSetting(@NonNull Context context, boolean enable) {
        SharedPrefsUtil.saveBoolean(context, PREF_LAST_READ_PAGE_SETTING, enable);
    }

    public static int getRecitationSetting(@NonNull Context context) {
        return SharedPrefsUtil.getInteger(context, PREF_RECITATION_SETTING, Constants.RECITATIONS.HAFS_ID);
    }

    /**
     * Changing the recitation will also reset the current reciter sheikh setting.
     *
     * @param context
     * @param recitationId
     * @return Whether the recitation setting has changed (and the reciter was reset) or not.
     */
    public static boolean persistRecitationSetting(@NonNull Context context, int recitationId) {
        if (recitationId != getRecitationSetting(context)) {
            resetReciterSheikhSetting(context);
            SharedPrefsUtil.saveInteger(context, PREF_RECITATION_SETTING, recitationId);
            return true;
        }
        return false;
    }

    @Nullable
    public static String getReciterSheikhSetting(@NonNull Context context) {
        return SharedPrefsUtil.getString(context, PREF_RECITER_SHEIKH_SETTING, null);
    }

    public static void persistReciterSheikhSetting(@NonNull Context context, String reciterSheikhId) {
        SharedPrefsUtil.saveString(context, PREF_RECITER_SHEIKH_SETTING, reciterSheikhId);
    }

    public static void resetReciterSheikhSetting(@NonNull Context context) {
        SharedPrefsUtil.clearPreference(context, PREF_RECITER_SHEIKH_SETTING);
    }

    public static boolean isFirstTimeWizardDone(@NonNull Context context) {
        return SharedPrefsUtil.getBoolean(context, PREF_FIRST_TIME_WIZARD_SHOW_FLAG, false);
    }

    public static void markFirstTimeWizardDone(@NonNull Context context) {
        SharedPrefsUtil.saveBoolean(context, PREF_FIRST_TIME_WIZARD_SHOW_FLAG, true);
    }

    @NonNull
    public static String getQuranTranslationLanguage(@NonNull Context context) {
        return SharedPrefsUtil.getString(context, PREF_QURAN_TRANSLATION_LANGUAGE
                , getAppLangSetting(context));
    }

    public static void persistQuranTranslationLanguage(@NonNull Context context, @NonNull String langCode) {
        SharedPrefsUtil.saveString(context, PREF_QURAN_TRANSLATION_LANGUAGE, langCode);
        SharedPrefsUtil.clearPreference(context, PREF_QURAN_TRANSLATION_BOOK);
    }

    @Nullable
    public static String getQuranTranslationBook(@NonNull Context context) {
        return SharedPrefsUtil.getString(context, PREF_QURAN_TRANSLATION_BOOK, null);
    }

    public static void persistQuranTranslationBook(@NonNull Context context, @NonNull String translationBookId) {
        SharedPrefsUtil.saveString(context, PREF_QURAN_TRANSLATION_BOOK, translationBookId);
    }

    @Nullable
    public static String getQuranBookDbName(@NonNull Context context) {
        return SharedPrefsUtil.getString(context, "book_db_name", null);
    }

    public static void persistBookDbName(@NonNull Context context, @NonNull String dbName) {
        SharedPrefsUtil.saveString(context, "book_db_name", dbName);
    }

    @Nullable
    public static String getQuranBookName(@NonNull Context context) {
        return SharedPrefsUtil.getString(context, "book_db_name", null);
    }

    public static void persistBookName(@NonNull Context context, @NonNull String dbName) {
        SharedPrefsUtil.saveString(context, "book_db_name", dbName);
    }

}
