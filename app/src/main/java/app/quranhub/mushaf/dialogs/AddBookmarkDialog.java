package app.quranhub.mushaf.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thebluealliance.spectrum.SpectrumPalette;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import app.quranhub.mushaf.adapter.BookmarkTypeAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import app.quranhub.R;
import app.quranhub.mushaf.data.entity.BookmarkType;
import app.quranhub.mushaf.listener.ItemSelectionListener;
import app.quranhub.utils.DialogUtil;

public class AddBookmarkDialog extends DialogFragment implements ItemSelectionListener<Integer> {

    private final static String BOOKMARK_TYPES_ARGS = "BOOKMARK_TYPES_ARGS";
    private final static String IS_EDITABLE = "IS_EDITABLE";
    private View dialogView;
    private Dialog dialog;
    private AddBookmarkListener listener;
    private int selectedType;
    private List<BookmarkType> bookmarkTypes;
    private boolean isAddCustom = false;
    private BookmarkTypeAdapter adapter;
    private int colorIndex = 0;

    @BindView(R.id.btn_show)
    Button showBtn;
    @BindView(R.id.bookmark_types_rv)
    RecyclerView bookmarkTypesRv;
    @BindView(R.id.add_custom_group)
    ConstraintLayout addCustomGroup;
    @BindView(R.id.bookmark_title_et)
    EditText customBookmarkTitleEt;
    @BindView(R.id.palette)
    SpectrumPalette palette;
    @BindView(R.id.custom_bookmark_group)
    Group customBookmarkGroup;
    @BindView(R.id.add_custom_check_iv)
    ImageView customBookmarkCheck;

    public static AddBookmarkDialog getInstance(List<BookmarkType> types, boolean isEditable) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BOOKMARK_TYPES_ARGS, (ArrayList<? extends Parcelable>) types);
        bundle.putBoolean(IS_EDITABLE, isEditable);
        AddBookmarkDialog dialog = new AddBookmarkDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (AddBookmarkListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("The parent fragment of BookmarkEditDialog (" +
                    getParentFragment().getClass().getSimpleName() + ") must implement the BookmarkFilterListener interface");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DialogUtil.wrapDialogHeight(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.add_bookmark_dialog, null);
        ButterKnife.bind(this, dialogView);
        getArgs();
        initializeDialog();
        observeOnSelectedColor();
        return dialog;
    }

    private void observeOnSelectedColor() {
        int[] colors = getActivity().getResources().getIntArray(R.array.bookmark_colors);
        palette.setSelectedColor(colors[0]);
        palette.setOnColorSelectedListener(color -> {
            for(int i = 0 ;i < colors.length ; i++) {
                if(color == colors[i]) {
                    colorIndex = i;
                    break;
                }
            }
            Log.d("yy8", "observeOnSelectedColor: " + color);
        });
    }

    private void getArgs() {
        if (getArguments() != null) {
            bookmarkTypes = getArguments().getParcelableArrayList(BOOKMARK_TYPES_ARGS);
            if (!getArguments().getBoolean(IS_EDITABLE)) {
                addCustomGroup.setVisibility(View.GONE);
                customBookmarkGroup.setVisibility(View.GONE);
                showBtn.setText(getString(R.string.show));
            }
        }
    }

    public void initializeDialog() {
        dialog = new Dialog(requireActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        adapter = new BookmarkTypeAdapter(bookmarkTypes, getActivity(), this);
        bookmarkTypesRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        bookmarkTypesRv.setAdapter(adapter);
        selectedType = 1;
    }

    @OnClick(R.id.add_custom_group)
    public void onAddCustomBookmark() {
        customBookmarkCheck.setVisibility(View.VISIBLE);
        customBookmarkGroup.setVisibility(View.VISIBLE);
        adapter.hideCheck();
        isAddCustom = true;
    }


    @OnClick(R.id.btn_show)
    void onShowFilterList() {
        if (isAddCustom) {
            if (customBookmarkTitleEt.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), getString(R.string.enter_bookmark_title), Toast.LENGTH_LONG).show();
            } else {
                BookmarkType type = new BookmarkType(bookmarkTypes.size() + 1, customBookmarkTitleEt.getText().toString(), colorIndex);
                listener.addCustomBookmark(type);
                dismiss();
            }
        } else {
            listener.addNormalBookmark(selectedType);
            dismiss();
        }
    }

    @OnClick(R.id.btn_back)
    void onBackDialog() {
        dialog.dismiss();
    }

    @Override
    public void onSelectItem(Integer type) {
        selectedType = type;
        isAddCustom = false;
        customBookmarkCheck.setVisibility(View.GONE);
    }

    public interface AddBookmarkListener {
        void addNormalBookmark(int bookmarkType);

        void addCustomBookmark(BookmarkType type);
    }
}
