package app.quranhub.mushaf.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Objects;

import app.quranhub.R;
import app.quranhub.mushaf.data.entity.Aya;
import app.quranhub.mushaf.model.RepeatModel;
import app.quranhub.mushaf.model.SuraVersesNumber;
import app.quranhub.utils.DialogUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AyaRepeatDialog extends DialogFragment {

    private View dialogView;
    private Dialog dialog;
    private AyaRepeateListener listener;
    private static final String ARG_SURA_VERSES_NUMBER = "ARG_SURA_VERSES_NUMBER";
    private static final String ARG_SELECTED_SURA = "ARG_SELECTED_SURA";
    private ArrayList<SuraVersesNumber> suraVersesNumberArrayList;
    private Aya selectedAya;
    private int lastAyaInPage;
    private int maxFromAyaNumber, maxToAyaNumber;
    private int fromSuraNumber, toSuraNumber;
    private boolean fromUser = false;

    @BindView(R.id.from_aya_sp)
    Spinner fromAyaSpinner;
    @BindView(R.id.to_aya_sp)
    Spinner toAyaSpinner;
    @BindView(R.id.from_et)
    EditText fromAyaEt;
    @BindView(R.id.to_et)
    EditText toAyaEt;
    @BindView(R.id.aya_group_number_et)
    EditText ayaGroupNumEt;
    @BindView(R.id.aya_repeat_number_et)
    EditText ayaRepeatNumEt;
    @BindView(R.id.delay_et)
    EditText delayEt;
    @BindView(R.id.repeat_btn)
    Button repeatBtn;


    public static AyaRepeatDialog getInstance(ArrayList<SuraVersesNumber> suraVersesNumberArrayList, Aya selectedAya) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_SELECTED_SURA, selectedAya);
        bundle.putParcelableArrayList(ARG_SURA_VERSES_NUMBER, suraVersesNumberArrayList);
        AyaRepeatDialog dialog = new AyaRepeatDialog();
        dialog.setArguments(bundle);
        return dialog;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AyaRepeateListener) getParentFragment();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.aya_repeat_dialog, null);
        ButterKnife.bind(this, dialogView);
        initializeDialog();
        getArgs();
        setFromToViews();
        observeSpinnerSelection();
        observeOnInputEditText();
        return dialog;
    }

    private void observeOnInputEditText() {

        fromAyaEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty())
                    return;
                if (Integer.parseInt(s.toString()) > maxFromAyaNumber) {
                    fromAyaEt.setError(getString(R.string.enter_valid_aya));
                } else {
                    fromAyaEt.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        toAyaEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty())
                    return;
                if (Integer.parseInt(s.toString()) > maxToAyaNumber) {
                    toAyaEt.setError(getString(R.string.enter_valid_aya));
                } else {
                    toAyaEt.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void observeSpinnerSelection() {


        fromAyaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent != null && parent.getChildAt(0) != null)
                    ((TextView) parent.getChildAt(0)).setTextColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.white_color));
                maxFromAyaNumber = suraVersesNumberArrayList.get(position).getAyas();
                if (fromUser) {
                    fromAyaEt.setText("1");

                    fromSuraNumber = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        toAyaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent != null && parent.getChildAt(0) != null)
                    ((TextView) parent.getChildAt(0)).setTextColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.white_color));
                maxToAyaNumber = suraVersesNumberArrayList.get(position).getAyas();
                if (fromUser) {
                    toAyaEt.setText(String.valueOf(maxToAyaNumber));
                    toSuraNumber = position;
                } else {
                    fromUser = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }


    private void setFromToViews() {
        String[] surahs = getResources().getStringArray(R.array.sura_name);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, surahs);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromAyaSpinner.setAdapter(dataAdapter);
        toAyaSpinner.setAdapter(dataAdapter);
        if (selectedAya != null) {
            lastAyaInPage = suraVersesNumberArrayList.get(selectedAya.getSura() - 1).getAyas();
            fromAyaEt.setText(String.valueOf(selectedAya.getSuraAya()));
            toAyaEt.setText(String.valueOf(lastAyaInPage));
            fromAyaSpinner.setSelection(selectedAya.getSura() - 1);
            toAyaSpinner.setSelection(selectedAya.getSura() - 1);
            maxFromAyaNumber = suraVersesNumberArrayList.get(selectedAya.getSura() - 1).getAyas();
            maxToAyaNumber = maxFromAyaNumber;
            toSuraNumber = fromSuraNumber = selectedAya.getSura() - 1;
        } else {
            fromAyaEt.setText("1");
            toAyaEt.setText("1");
            fromAyaSpinner.setSelection(0);
            toAyaSpinner.setSelection(0);
            maxFromAyaNumber = suraVersesNumberArrayList.get(0).getAyas();
            maxToAyaNumber = maxFromAyaNumber;
            toSuraNumber = fromSuraNumber = 0;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        DialogUtil.adjustDialogSize(this, 0.8f,
                0.8f, 0.7f,
                0.9f);

    }

    public void initializeDialog() {
        dialog = new Dialog(requireActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
    }

    private void getArgs() {
        if (getArguments() != null) {
            selectedAya = getArguments().getParcelable(ARG_SELECTED_SURA);
            suraVersesNumberArrayList = getArguments().getParcelableArrayList(ARG_SURA_VERSES_NUMBER);
        }
    }

    @OnClick(R.id.repeat_btn)
    public void onClickRepeat() {
        if (fromAyaEt.getError() != null || toAyaEt.getError() != null) {
            Toast.makeText(getActivity(), getString(R.string.enter_valid_aya), Toast.LENGTH_LONG).show();
        } else if (fromAyaEt.getText().toString().isEmpty() || toAyaEt.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.enter_repeat_interval), Toast.LENGTH_LONG).show();
        } else if (fromSuraNumber > toSuraNumber) {
            Toast.makeText(getActivity(), getString(R.string.invalid_repeat_interval), Toast.LENGTH_LONG).show();
        } else if (fromSuraNumber == toSuraNumber && Integer.parseInt(fromAyaEt.getText().toString()) > Integer.parseInt(toAyaEt.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.enter_valid_aya), Toast.LENGTH_LONG).show();
        } else {
            RepeatModel repeatModel = new RepeatModel();
            repeatModel.setFromSura(fromSuraNumber + 1);
            repeatModel.setFromAyaId(getFromAyaId(Integer.parseInt(fromAyaEt.getText().toString()), fromSuraNumber + 1));
            repeatModel.setFromAya(Integer.parseInt(fromAyaEt.getText().toString()));
            repeatModel.setToSura(toSuraNumber + 1);
            repeatModel.setToAyaId(getToAyaId(Integer.parseInt(toAyaEt.getText().toString()), toSuraNumber + 1));
            repeatModel.setToAya(Integer.parseInt(toAyaEt.getText().toString()));
            repeatModel.setGroupRepeatNum(ayaGroupNumEt.getText().toString().trim().isEmpty() || Integer.parseInt(ayaGroupNumEt.getText().toString()) == 0
                    ? 1 : Integer.parseInt(ayaGroupNumEt.getText().toString().trim()));

            repeatModel.setAyaRepeatNum(ayaRepeatNumEt.getText().toString().trim().isEmpty() || Integer.parseInt(ayaRepeatNumEt.getText().toString()) == 0
                    ? 1 : Integer.parseInt(ayaRepeatNumEt.getText().toString().trim()));

            repeatModel.setDelayTime(delayEt.getText().toString().trim().isEmpty()
                    ? 1 : Integer.parseInt(delayEt.getText().toString().trim()));
            listener.onAyasRepeate(repeatModel);
            dismiss();
        }
    }

    private int getFromAyaId(int fromAya, int fromSura) {
        int fromAyaId = fromAya;
        for (int i = 1; i < fromSura; i++) {
            fromAyaId += suraVersesNumberArrayList.get(i - 1).getAyas();
        }
        return fromAyaId;
    }

    private int getToAyaId(int toAya, int toSura) {
        int toAyaId = toAya;
        for (int i = 1; i < toSura; i++) {
            toAyaId += suraVersesNumberArrayList.get(i - 1).getAyas();
        }
        return toAyaId;
    }


    @OnClick(R.id.btn_back)
    public void onClickBack() {
        dismiss();
    }

    public interface AyaRepeateListener {
        void onAyasRepeate(RepeatModel repeatModel);
    }
}
