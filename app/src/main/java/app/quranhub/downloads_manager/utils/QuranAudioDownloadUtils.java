package app.quranhub.downloads_manager.utils;


import androidx.annotation.Nullable;

import java.io.File;

import app.quranhub.Constants;

public final class QuranAudioDownloadUtils {

    private QuranAudioDownloadUtils() { /* Prevent instantiation */ }

    /**
     * Generates & returns the Quran audio file download URL relative path for the given args.
     *
     * @param recitationId Recitation ID as in {@link Constants.RECITATIONS}.
     * @param sheikhId     Sheikh ID.
     * @param sura         Sura number (one-based index).
     * @param aya          Aya number in sura (one-based index).
     * @return returns the file download path as a String, or {@code null} if one of the provided args
     * is incorrect.
     */
    @Nullable
    public static String getDownloadUrlPath(int recitationId, String sheikhId, int sura, int aya) {
        /*
        Aya audio file download path should be on the format:
             /{recitation_key}/sound/{sheikh_id}/{filename}
        */

        StringBuilder sb = new StringBuilder(File.separator);

        // recitation part
        if (recitationId == Constants.RECITATIONS.HAFS_ID)
            sb.append(Constants.RECITATIONS.HAFS_KEY);
        else if (recitationId == Constants.RECITATIONS.WARSH_ID)
            sb.append(Constants.RECITATIONS.WARSH_KEY);
        else return null;
        sb.append(File.separator);


        sb.append("sound");
        sb.append(File.separator);

        // sheikh part
        if (sheikhId != null) sb.append(sheikhId);
        else return null;
        sb.append(File.separator);

        // file name part
        String fileName = QuranAudioFileUtils.getFileName(sura, aya);
        if (fileName != null) sb.append(fileName);
        else return null;

        return sb.toString();
    }
}
