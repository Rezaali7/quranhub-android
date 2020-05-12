package app.quranhub.mushaf.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import app.quranhub.Constants;
import app.quranhub.R;
import app.quranhub.utils.PreferencesUtils;
import app.quranhub.utils.RecorderMediaUtil;


public class AyaRecorderPlayerDialog extends DialogFragment implements RecorderMediaUtil.MediaPlayerCallback {

    private View dialogView;
    private Dialog dialog;
    private static final String ARG_AYA_ID = "ARG_AYA_ID";
    private AyaRecorderPlayerListener listener;
    private RecorderMediaUtil recorderMediaUtil;
    private String outputRecorderPath;
    private int ayaId;
    private boolean isPlaying = false, userIsSeeking = false, firstPlay = true;
    private int userSelectedPosition;

    @BindView(R.id.play_iv)
    ImageView playIv;
    @BindView(R.id.recorder_progress)
    SeekBar progressRecorder;
    @BindView(R.id.recorder_time_tv)
    TextView recorderTime;


    public static AyaRecorderPlayerDialog getInstance(int ayaId) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_AYA_ID, ayaId);
        AyaRecorderPlayerDialog dialog = new AyaRecorderPlayerDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AyaRecorderPlayerListener) getParentFragment();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.play_aya_recorder_dialog, null);
        ButterKnife.bind(this, dialogView);
        intializeDialog();
        setRecordingFile();
        initSoundMedia();
        getPrevState(savedInstanceState);
        listenToSeekbarChanges();
        return dialog;
    }

    private void getPrevState(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            recorderMediaUtil.seekTo(savedInstanceState.getInt("player_position"));
            isPlaying = savedInstanceState.getBoolean("is_playing");
            restorePlayingState();
        }
    }

    private void restorePlayingState() {
        if(isPlaying) {
            playIv.setImageResource(R.drawable.ic_pause);
            recorderMediaUtil.play();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_playing", isPlaying);
        outState.putInt("player_position", recorderMediaUtil.getCurrentPosition());

    }


    public void intializeDialog() {
        dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        dialog.setContentView(dialogView);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        if (getArguments() != null) {
            ayaId = getArguments().getInt(ARG_AYA_ID);
        }

    }

    private void setRecordingFile() {
        int recitation = PreferencesUtils.getRecitationSetting(getActivity());
        File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC), Constants.DIRECTORY.AYA_VOICE_RECORDER
                + File.separator + recitation + File.separator
                + ayaId + ".3gp");
        if (file.exists()) {
            outputRecorderPath = file.getPath();
        } else {
            listener.onClickDeleteRecorder();
            Toast.makeText(getActivity(), getString(R.string.file_not_exist), Toast.LENGTH_LONG).show();
            dismiss();
        }
    }

    private void listenToSeekbarChanges() {
        progressRecorder.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                userIsSeeking = true;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    userSelectedPosition = progress;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                userIsSeeking = false;
                recorderMediaUtil.seekTo(userSelectedPosition);
            }
        });
    }

    private void initSoundMedia() {
        if(outputRecorderPath != null) {
            recorderMediaUtil = new RecorderMediaUtil();
            recorderMediaUtil.setMediaPlayerCallback(this);
            recorderMediaUtil.setAudioPath(outputRecorderPath);
        }
    }

    @OnClick(R.id.remove_record_iv)
    public void onRemoveRecorder() {
        recorderMediaUtil.release();
        listener.onClickDeleteRecorder();
        dismiss();
    }

    @OnClick(R.id.play_iv)
    public void onPlayRecorder() {
        if (isPlaying) {
            playIv.setImageResource(R.drawable.player_play_white_ic);
            recorderMediaUtil.pause();
        } else {
            playIv.setImageResource(R.drawable.ic_pause);
            recorderMediaUtil.play();
            recorderMediaUtil.startUpdatingAudioTime();
            if (firstPlay) {
                firstPlay = false;
                recorderTime.setText("0:00");
            }
        }
        isPlaying = !isPlaying;
    }

    @Override
    public void onGetMaxDuration(int duration) {
        progressRecorder.setMax(duration);
    }

    @Override
    public void onPositionChanged(int position) {
        if (!userIsSeeking) {
            if (Build.VERSION.SDK_INT >= 24) {
                progressRecorder.setProgress(position, true);
            } else {
                progressRecorder.setProgress(position);
            }
        }
    }

    @Override
    public void onUpdatedTime(String time) {
        recorderTime.setText(time);
    }

    @Override
    public void onStateChanged(int state) {
        if (state == State.COMPLETED) {
            progressRecorder.setProgress(0);
            isPlaying = false;
            firstPlay = true;
            playIv.setImageResource(R.drawable.player_play_white_ic);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!getActivity().isChangingConfigurations() && recorderMediaUtil != null) {
            recorderMediaUtil.release();
        }
    }

    public interface AyaRecorderPlayerListener {
        void onClickDeleteRecorder();
    }
}
