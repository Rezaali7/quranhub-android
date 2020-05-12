package app.quranhub.mushaf.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.Objects;

import app.quranhub.R;
import app.quranhub.mushaf.adapter.FilterAdapter;
import app.quranhub.mushaf.listener.ItemSelectionListener;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesFilterDialog extends DialogFragment implements FilterAdapter.OptionClickListener {

    private static final String NOTE_TYPE_ARGS = "NOTE_TYPE_ARGS";
    private View dialogView;
    private Dialog dialog;
    private ItemSelectionListener<Integer> listener;
    private int selectedOption;
    private FilterAdapter adapter;
    private String[] options;

    @BindView(R.id.note_filter_rv)
    RecyclerView filterRv;

    public static NotesFilterDialog getInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(NOTE_TYPE_ARGS, type);
        NotesFilterDialog dialog = new NotesFilterDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (ItemSelectionListener) getParentFragment();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.note_filter_dialog, null);
        ButterKnife.bind(this, dialogView);
        initializeDialog();
        setFilterOptions();
        initViews();
        return dialog;
    }


    /*@Override
    public void onResume() {
        super.onResume();

        DialogUtil.adjustDialogSize(this, DialogUtil.DIALOG_STD_WIDTH_SCREEN_RATIO_PORTRAIT, 0.8f
                , DialogUtil.DIALOG_STD_WIDTH_SCREEN_RATIO_LANDSCAPE, DialogUtil.DIALOG_STD_HEIGHT_SCREEN_RATIO_LANDSCAPE);
    }*/


    private void setFilterOptions() {
        options = new String[]{
                getString(R.string.all_types),
                getString(R.string.general_comment),
                getString(R.string.momerize_mistake),
                getString(R.string.tajweed_mistake)
        };
    }

    private void initViews() {

        filterRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FilterAdapter(Arrays.asList(options), options[selectedOption], this, 0);
        filterRv.setAdapter(adapter);

    }


    public void initializeDialog() {
        dialog = new Dialog(requireActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        if (getArguments() != null) {
            selectedOption = getArguments().getInt(NOTE_TYPE_ARGS);
        }
    }

    /*@OnClick(R.id.btn_back)
    public void onClickBack() {
        dismiss();
    }

    @OnClick(R.id.btn_show)
    public void onShowFilter() {
        listener.onSelectItem(selectedOption);
        dismiss();
    }*/

    @Override
    public void onOptionClick(String optionName, int optionIndex) {
        //selectedOption = optionIndex;
        listener.onSelectItem(optionIndex);
        dismiss();
    }
}
