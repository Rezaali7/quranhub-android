package app.quranhub.mushaf.dialogs;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import app.quranhub.Constants;
import app.quranhub.R;
import app.quranhub.mushaf.data.entity.Note;
import app.quranhub.utils.DialogUtil;
import app.quranhub.utils.RecorderMediaUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddNoteDialog extends DialogFragment implements RecorderMediaUtil.MediaPlayerCallback {

    @BindView(R.id.note_type_group)
    RadioGroup noteRadioGroup;
    @BindView(R.id.add_note_et)
    EditText addNoteEt;
    @BindView(R.id.add_recorder_iv)
    ImageView addRecorderIv;
    @BindView(R.id.voice_status_tv)
    TextView voiceStatusTv;
    @BindView(R.id.voice_timer_tv)
    TextView voiceTiemrTv;
    @BindView(R.id.recorder_chronometer)
    Chronometer chronometer;
    @BindView(R.id.play_iv)
    ImageView playIv;
    @BindView(R.id.recorder_progress)
    SeekBar progressRecorder;
    @BindView(R.id.record_group)
    Group recordGroup;
    @BindView(R.id.save_btn)
    Button saveButton;
    @BindView(R.id.tv_title)
    TextView dialogHeaderTv;


    private boolean isRecord = false, isPlaying = false, isRecorderAttatched = false, userIsSeeking = false, firstPlay = true;
    private int userSelectedPosition;
    private View dialogView;
    private Dialog dialog;
    private AddNoteListener listener;
    private String[] permissions;
    private String outputRecorderPath;
    private int ayaId;
    private MediaRecorder audioRecorder;
    private RecorderMediaUtil recorderMediaUtil;
    private File outputFile;
    private Note note;
    private boolean isEditable = false;

    public static AddNoteDialog getInstance(int ayaId) {
        Bundle bundle = new Bundle();
        bundle.putInt("aya_id", ayaId);
        AddNoteDialog dialog = new AddNoteDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    public static AddNoteDialog getInstance(Note selectedAyaNote) {
        Bundle bundle = new Bundle();
        bundle.putInt("aya_id", selectedAyaNote.getAyaId());
        bundle.putParcelable("selected_aya", selectedAyaNote);
        AddNoteDialog dialog = new AddNoteDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AddNoteListener) getParentFragment();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.add_note_dialog, null);
        ButterKnife.bind(this, dialogView);
        initializeDialog();
        getArgs();
        listenToSeekbarChanges();
        return dialog;
    }


    @Override
    public void onResume() {
        super.onResume();

        DialogUtil.adjustDialogSize(this, DialogUtil.DIALOG_STD_WIDTH_SCREEN_RATIO_PORTRAIT, 0.8f
                , DialogUtil.DIALOG_STD_WIDTH_SCREEN_RATIO_LANDSCAPE, DialogUtil.DIALOG_STD_HEIGHT_SCREEN_RATIO_LANDSCAPE);
    }


    public void initializeDialog() {
        dialog = new Dialog(requireActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        permissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    private void getArgs() {
        if (getArguments() != null) {
            ayaId = getArguments().getInt("aya_id");
            note = getArguments().getParcelable("selected_aya");
            createRecordingFile();
            if (note != null) {
                isEditable = true;
                setEditView();
                saveButton.setText(getString(R.string.save));
            }
        }
    }

    private void setEditView() {
        dialogHeaderTv.setText(getString(R.string.edit_note));
        ((RadioButton) noteRadioGroup.getChildAt(note.getNoteType())).setChecked(true);
        if (note.getNoteText() != null) {
            addNoteEt.setText(note.getNoteText());
        }
        if (!note.getNoteRecorderPath().isEmpty()) {
            setAudioViewsVisible();
            isRecorderAttatched = true;
            initSoundMedia();
        }

    }

    private void setAudioViewsVisible() {
        voiceTiemrTv.setVisibility(View.VISIBLE);
        recordGroup.setVisibility(View.VISIBLE);
        addRecorderIv.setVisibility(View.INVISIBLE);
        voiceStatusTv.setText(getString(R.string.voice_listen));
    }

    private void createRecordingFile() {

        File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC), Constants.DIRECTORY.NOTE_VOICE_RECORDER);

        if (!file.exists()) {
            file.mkdir();
        }
        outputRecorderPath = file.getPath() + File.separator + ayaId + ".3gp";
        outputFile = new File(outputRecorderPath);
    }


    @OnClick(R.id.save_btn)
    public void onAddNote() {
        if (TextUtils.isEmpty(addNoteEt.getText()) && !isRecorderAttatched && !isRecord) {
            Toast.makeText(getActivity(), getString(R.string.note_empty), Toast.LENGTH_LONG).show();
        } else {
            String path = "";
            if (isRecorderAttatched || isRecord) {
                path = outputRecorderPath;
            } else {
                deleteRecorderFile();
            }
            int selectedType = noteRadioGroup.indexOfChild(dialogView.findViewById(noteRadioGroup.getCheckedRadioButtonId()));
            listener.onAddNote(new Note(ayaId, selectedType, addNoteEt.getText().toString(), path), isEditable);
            dismiss();
        }
    }

    @OnClick(R.id.cancel_btn)
    public void onCancel() {
        listener.onDismissDialog();
        dismiss();
    }

    @OnClick(R.id.add_recorder_iv)
    public void onClickRecord() {
        if (isRecord) {
            setAudioViewsVisible();
            stopTimer();
            stopRecorderMedia();
            initSoundMedia();
            isRecord = false;
            isRecorderAttatched = true;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getActivity(), permissions[0]) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getActivity(), permissions[1]) == PackageManager.PERMISSION_GRANTED) {
                    initRecording();
                } else {
                    requestPermissions(permissions, 1);
                }
            } else {
                initRecording();
            }
        }

    }

    private void initSoundMedia() {
        recorderMediaUtil = new RecorderMediaUtil();
        recorderMediaUtil.setMediaPlayerCallback(this);
        recorderMediaUtil.setAudioPath(outputRecorderPath);
    }

    private void stopRecorderMedia() {
        if (audioRecorder != null) {
            if (isRecord) {
                audioRecorder.stop();
            }
            audioRecorder.release();
        }
        if (recorderMediaUtil != null) {
            recorderMediaUtil.release();
        }
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
                voiceTiemrTv.setText("0:00");
            }
        }

        isPlaying = !isPlaying;
    }

    @OnClick(R.id.remove_record_iv)
    public void onRemoveRecord() {
        recordGroup.setVisibility(View.GONE);
        voiceTiemrTv.setVisibility(View.GONE);
        addRecorderIv.setVisibility(View.VISIBLE);
        addRecorderIv.setBackgroundResource(R.drawable.corner_primary_dialog);
        voiceStatusTv.setText(getString(R.string.add_voice));
        recorderMediaUtil.release();
        isRecorderAttatched = false;
    }

    public void deleteRecorderFile() {
        if (outputFile.exists()) {
            outputFile.delete();
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
                if (fromUser) {
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


    private void initRecording() {
        isRecord = true;
        addRecorderIv.setBackgroundResource(R.drawable.red_corner);
        voiceStatusTv.setText(getString(R.string.voice_recorded));
        startTimer();
        startRecord();
    }

    private void startRecord() {
        audioRecorder = new MediaRecorder();
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        audioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        audioRecorder.setOutputFile(outputRecorderPath);
        try {
            audioRecorder.prepare();
            audioRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopTimer() {
        chronometer.setVisibility(View.GONE);
        chronometer.stop();
    }

    private void startTimer() {
        chronometer.setVisibility(View.VISIBLE);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isGranted = true;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
                break;
            }
        }
        if (isGranted) {
            initRecording();
        } else {
            Toast.makeText(getActivity(), getString(R.string.accept_perm), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listener.onDismissDialog();
        stopRecorderMedia();
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
    public void onStateChanged(int state) {
        if (state == State.COMPLETED) {
            progressRecorder.setProgress(0);
            isPlaying = false;
            firstPlay = true;
            playIv.setImageResource(R.drawable.player_play_white_ic);
        }
    }

    @Override
    public void onUpdatedTime(String time) {
        voiceTiemrTv.setText(time);
    }

    public interface AddNoteListener {
        void onAddNote(Note note, boolean isEditable);

        void onDismissDialog();
    }
}
