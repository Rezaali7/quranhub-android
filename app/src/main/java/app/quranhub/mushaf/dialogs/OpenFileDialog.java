package app.quranhub.mushaf.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;
import app.quranhub.R;


public class OpenFileDialog extends DialogFragment {

    private View dialogView;
    private Dialog dialog;
    private OpenFileListener listener;
    public static final int IN_APP = 1;
    public static final int OUT_APP = 2;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OpenFileListener) getParentFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.open_file_dialog, null);
        ButterKnife.bind(this, dialogView);
        intializeDialog();
        return dialog;
    }


    @OnClick(R.id.in_app)
    public void openPdfInApp() {
        dialog.cancel();
        listener.onOpefFile(IN_APP);
    }

    @OnClick(R.id.out_app)
    public void openPdfOutApp() {
        dialog.cancel();
        listener.onOpefFile(OUT_APP);
    }

    private void intializeDialog() {
        dialog = new Dialog(Objects.requireNonNull(getActivity()));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
    }

    public interface OpenFileListener {
        void onOpefFile(int openType);
    }
}
