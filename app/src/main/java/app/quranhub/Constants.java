package app.quranhub;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public final class Constants {


    private Constants() { /* Prevent instantiation */ }


    public static final String BASE_URL = "http://api.haramin.gplanet.tech";

    public interface QURAN {
        String BASE_URL = "http://quran.haramin.gplanet.tech";
        String HAFS_IMAGE_BASE_URL = BASE_URL + "/hafs/images/";
        String WARSH_IMAGE_BASE_URL = BASE_URL + "/warsh/images/";

        int NUM_OF_PAGES = 604;

        // quran page sizes in pixels
        int HAFS_PAGE_ORIGINAL_WIDTH = 622;
        int HAFS_PAGE_ORIGINAL_HEIGHT = 917;
        int WARSH_PAGE_ORIGINAL_WIDTH = 620;
        int WARSH_PAGE_ORIGINAL_HEIGHT = 1005;
        int NUM_OF_VERSES = 6236;
    }

    public interface BOOKMARK_TYPE {
        int FAVORITE = 1;
        int RECITING = 2;
        int NOTE = 3;
        int MEMORIZE = 4;
    }

    public interface DIRECTORY {
        String ROOT_PUBLIC = "QuranHub";
        String LIBRARY_PUBLIC = ROOT_PUBLIC + File.separator + "Library";

        String NOTE_VOICE_RECORDER = "Note_Recorder";
        String AYA_VOICE_RECORDER = "Aya_Recorder";
        String QURAN_AUDIO = ".quran_audio";
    }

    public interface SUPPORTED_LANGUAGES {
        String ENGLISH_CODE = "en";
        String ARABIC_CODE = "ar";
        String SPANISH_CODE = "es";
        String FRENCH_CODE = "fr";
        String HAUSA_CODE = "ha";
        String INDONESIAN_CODE = "in";
        String URDU_CODE = "ur";

        String DEFAULT_APP_LANGUAGE = ENGLISH_CODE;

        /* It's important that the indices of languages is the same in CODES, NAMES_STR_IDS & FLAGS_DRAWABLE_IDS */
        List<String> CODES = Arrays.asList(ENGLISH_CODE, ARABIC_CODE, SPANISH_CODE, FRENCH_CODE
                , HAUSA_CODE, INDONESIAN_CODE, URDU_CODE);
        int[] NAMES_STR_IDS = {R.string.english_language, R.string.arabic_language, R.string.spanish_language
                , R.string.french_language, R.string.hausa_language, R.string.indonesian_language, R.string.urdu_language};
        int[] FLAGS_DRAWABLE_IDS = {R.drawable.flag_en, R.drawable.flag_ar, R.drawable.flag_es
                , R.drawable.flag_fr, R.drawable.flag_ha, R.drawable.flag_in, R.drawable.flag_ur};
    }

    public interface RECITATIONS {
        String HAFS_KEY = "hafs";
        String WARSH_KEY = "warsh";

        int HAFS_ID = 0;
        int WARSH_ID = 1;

        /* It's important that the index of any recitation name is the same as the ID integer given for it above */
        int[] NAMES_STR_IDS = {R.string.hafs_recitation, R.string.warsh_recitation};
    }

}
