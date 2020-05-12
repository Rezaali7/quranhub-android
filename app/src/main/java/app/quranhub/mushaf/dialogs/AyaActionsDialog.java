package app.quranhub.mushaf.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import app.quranhub.mushaf.model.BookmarkModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import app.quranhub.Constants;
import app.quranhub.R;

public class AyaActionsDialog extends DialogFragment {

    private static final String TAG = AyaActionsDialog.class.getSimpleName();

    public static final String ARG_Y_LOCATION = "ARG_Y_LOCATION";

    private int yLocation;

    private View dialogView;
    private Dialog dialog;
    private AyaPropertiesListener ayaPropertiesListener;
    @BindView(R.id.bookmark_iv)
    ImageView bookmarkIv;
    @BindView(R.id.note_iv)
    ImageView noteIv;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof AyaPropertiesListener) {
            ayaPropertiesListener = (AyaPropertiesListener) getParentFragment();
        } else {
            throw new ClassCastException(
                    getParentFragment().getClass().getSimpleName() + " must implement AyaActionsDialog#AyaPropertiesListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            yLocation = getArguments().getInt(ARG_Y_LOCATION);
        } else {
            Log.w(TAG, "AyaActionsDialog : No arguments specified");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.aya_properties_dialog, null);
        ButterKnife.bind(this, dialogView);
        intializeDialog();
        return dialog;
    }

    public void intializeDialog() {
        dialog = new Dialog(getActivity());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        layoutParams.y = yLocation;
        dialog.setContentView(dialogView);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void setBookmarkTypeIcon(BookmarkModel bookmarkModel) {
        // handle if set image after orientation change
        if (bookmarkIv == null)
            return;

        if (bookmarkModel.getBookmarkTypeId() == Constants.BOOKMARK_TYPE.NOTE) {
            bookmarkIv.setImageResource(R.drawable.bookmark_green_selected);
        } else if (bookmarkModel.getBookmarkTypeId() == Constants.BOOKMARK_TYPE.MEMORIZE) {
            bookmarkIv.setImageResource(R.drawable.bookmark_red_selected);
        } else if (bookmarkModel.getBookmarkTypeId() == Constants.BOOKMARK_TYPE.RECITING) {
            bookmarkIv.setImageResource(R.drawable.bookmark_gold_selected);
        } else if (bookmarkModel.getBookmarkTypeId() == Constants.BOOKMARK_TYPE.FAVORITE) {
            bookmarkIv.setImageResource(R.drawable.fav_added__gold_ic);
        } else {    // CUSTOM BOOKMARK
            bookmarkIv.setImageResource(R.drawable.bookmark_green_selected);
            bookmarkIv.setColorFilter(getActivity().getResources().getIntArray(R.array.bookmark_colors)[bookmarkModel.getColorIndex()]);
        }
    }

    @OnClick(R.id.share_container)
    public void onShareClick() {
        dialog.dismiss();
        ayaPropertiesListener.onShareClick();
    }

    @OnClick(R.id.fasel_container)
    public void onFasilClick() {
        dialog.dismiss();
        ayaPropertiesListener.onFasilClick();
    }

    @OnClick(R.id.listen_container)
    public void onListenClick() {
        dialog.dismiss();
        ayaPropertiesListener.onListenClick();
    }

    @OnClick(R.id.tafseer_container)
    public void onTafserClick() {
        dialog.dismiss();
        ayaPropertiesListener.onTafserClick();
    }

    @OnClick(R.id.notes_container)
    public void onNotesClick() {
        dialog.dismiss();
        ayaPropertiesListener.onNoteClick();
    }

    public void setAyaHasNote() {
        if (noteIv == null)
            return;
        noteIv.setImageResource(R.drawable.notes_gold_sidemenu_ic);
    }


    public interface AyaPropertiesListener {

        void onShareClick();

        void onFasilClick();

        void onListenClick();

        void onTafserClick();

        void onNoteClick();
    }

}
