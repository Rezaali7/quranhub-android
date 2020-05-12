package app.quranhub.mushaf.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.Objects;

import app.quranhub.R;
import app.quranhub.mushaf.viewmodel.VoiceRecorderViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AyaRecorderDialog extends DialogFragment {

    private View dialogView;
    private Dialog dialog;
    private StopRecordingListener listener;
    private int ayaId;
    private VoiceRecorderViewModel voiceRecorderViewModel;
    private static final String ARG_AYA_ID = "ARG_AYA_ID";

    @BindView(R.id.recorder_chronometer)
    Chronometer chronometer;

    public static AyaRecorderDialog getInstance(int ayaId) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_AYA_ID, ayaId);
        AyaRecorderDialog recorderDialog = new AyaRecorderDialog();
        recorderDialog.setArguments(bundle);
        return recorderDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (StopRecordingListener) getParentFragment();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.aya_recorder_dialog, null);
        ButterKnife.bind(this, dialogView);
        getArgs();
        intializeDialog();
        initReorder(savedInstanceState == null);
        getPrevState(savedInstanceState);
        return dialog;
    }

    private void getPrevState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            startTimer(SystemClock.elapsedRealtime());
        } else {
            startTimer(SystemClock.elapsedRealtime() + savedInstanceState.getLong("chronometer_time"));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("chronometer_time", chronometer.getBase() - SystemClock.elapsedRealtime());
    }

    private void initReorder(boolean startRecord) {
        voiceRecorderViewModel = ViewModelProviders.of(this).get(VoiceRecorderViewModel.class);
        if (startRecord) {
            voiceRecorderViewModel.setAyaRecorderPath(ayaId, getActivity());
            voiceRecorderViewModel.startRecord();
        }
    }

    private void getArgs() {
        if (getArguments() != null) {
            ayaId = getArguments().getInt(ARG_AYA_ID);
        }
    }

    public void intializeDialog() {
        dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        dialog.setContentView(dialogView);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
    }


    private void startTimer(long base) {
        chronometer.setBase(base);
        chronometer.start();
    }

    @OnClick(R.id.stop_recording_view)
    public void onStopRecording() {
        voiceRecorderViewModel.releaseRecorder();
        chronometer.stop();
        listener.onStopRecording(voiceRecorderViewModel.getOutputRecorderPath());
        dismiss();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        chronometer.stop();
        if (!getActivity().isChangingConfigurations()) {
            voiceRecorderViewModel.releaseRecorder();
        }
    }

    public interface StopRecordingListener {
        void onStopRecording(String filePath);
    }
}
