package app.quranhub.mushaf.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import app.quranhub.R;
import app.quranhub.mushaf.adapter.BookmarkTypeAdapter;
import app.quranhub.mushaf.data.entity.BookmarkType;
import app.quranhub.mushaf.listener.ItemSelectionListener;
import app.quranhub.utils.DialogUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookmarkEditDialog extends DialogFragment implements ItemSelectionListener<Integer> {

    private final static String BOOKMARK_TYPES_ARGS = "BOOKMARK_TYPES_ARGS";
    private final static String FILTER_TYPE = "FILTER_TYPE";
    private final static String DIALOG_TYPE = "DIALOG_TYPE";
    private View dialogView;
    private Dialog dialog;
    private BookmarkFilterListener listener;
    public static final int ALL_BOOKMARK_FILTER = 0;
    private int selectedFilter, bookmarkColorIndex;
    private BookmarkTypeAdapter adapter;
    private List<BookmarkType> bookmarkTypes;
    private boolean editDialog;

    @BindView(R.id.all_bookmark_checkbox)
    ImageView allBookmarkIv;
    @BindView(R.id.bookmark_types_rv)
    RecyclerView typesRv;
    @BindView(R.id.all_bookmark)
    TextView allBookmarkTv;
    @BindView(R.id.btn_show)
    Button showBtn;


    public static BookmarkEditDialog getInstance(List<BookmarkType> types, int type, boolean editDialog) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BOOKMARK_TYPES_ARGS, (ArrayList<? extends Parcelable>) types);
        bundle.putBoolean(DIALOG_TYPE, editDialog);
        bundle.putInt(FILTER_TYPE, type);
        BookmarkEditDialog dialog = new BookmarkEditDialog();
        dialog.setArguments(bundle);
        return dialog;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (BookmarkFilterListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("The parent fragment of BookmarkEditDialog (" +
                    getParentFragment().getClass().getSimpleName() + ") must implement the BookmarkFilterListener interface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.bookmark_filter_dialog, null);
        ButterKnife.bind(this, dialogView);
        getArgs();
        initializeDialog();
        setDialogTypeViews();
        return dialog;
    }

    private void setDialogTypeViews() {
        if (editDialog) {
            showBtn.setText(getString(R.string.edit));
            allBookmarkIv.setVisibility(View.GONE);
            allBookmarkTv.setVisibility(View.GONE);
        }
    }


    private void getArgs() {
        if (getArguments() != null) {
            selectedFilter = getArguments().getInt(FILTER_TYPE, 0);
            bookmarkTypes = getArguments().getParcelableArrayList(BOOKMARK_TYPES_ARGS);
            editDialog = getArguments().getBoolean(DIALOG_TYPE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        DialogUtil.adjustDialogSize(this);
    }

    public void initializeDialog() {
        dialog = new Dialog(requireActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        adapter = new BookmarkTypeAdapter(bookmarkTypes, getActivity(), this);
        typesRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        typesRv.setAdapter(adapter);
        if (selectedFilter == 0) {
            adapter.hideCheck();
        } else {
            allBookmarkIv.setVisibility(View.GONE);
            adapter.setTypeCheck(selectedFilter);
        }
    }

    @OnClick(R.id.all_bookmark)
    void onSelectAllBookmark() {
        allBookmarkIv.setVisibility(View.VISIBLE);
        adapter.hideCheck();
        selectedFilter = ALL_BOOKMARK_FILTER;
    }


    @OnClick(R.id.btn_show)
    void onShowFilterList() {
        listener.onBookmarkFilter(selectedFilter, bookmarkColorIndex);
        dialog.dismiss();
    }

    @OnClick(R.id.btn_back)
    void onBackDialog() {
        dialog.dismiss();
    }

    @Override
    public void onSelectItem(Integer bookmarkType) {
        allBookmarkIv.setVisibility(View.GONE);
        selectedFilter = bookmarkType;
        bookmarkColorIndex = bookmarkTypes.get(selectedFilter - 1).getColorIndex();
    }


    public interface BookmarkFilterListener {
        void onBookmarkFilter(int filter, int colorIndex);
    }

}
