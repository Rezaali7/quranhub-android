package app.quranhub.mushaf.fragments;


import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import app.quranhub.R;
import app.quranhub.mushaf.listener.BookmarksListListener;
import app.quranhub.mushaf.listener.QuranNavigationCallbacks;
import app.quranhub.mushaf.presenter.BookmarksPresenter;
import app.quranhub.mushaf.presenter.BookmarksPresenterImp;
import app.quranhub.mushaf.view.BookmarksView;
import app.quranhub.utils.ScreenUtil;
import app.quranhub.utils.interfaces.ToolbarActionsListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class BookmarksFragment extends Fragment
        implements BookmarksView
        , BookmarksListListener, QuranNavigationCallbacks {

    private static final String TAG = BookmarksFragment.class.getSimpleName();

    private BookmarksPresenter presenter;

    private ToolbarActionsListener navDrawerListener;
    private QuranNavigationCallbacks quranNavigationCallbacks;
    private boolean isListEditable = true;

    @BindView(R.id.et_search)
    EditText searchEditText;
    @BindView(R.id.ib_finish_edit)
    ImageButton finishEditImageButton;
    @BindView(R.id.edit_btn)
    ImageButton editButton;
    @BindView(R.id.filter_btn)
    ImageButton filterButton;

    private Unbinder butterknifeUnbinder;

    private BookmarksListFragment bookmarksListFragment;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BookmarksFragment.
     */
    public static BookmarksFragment newInstance() {
        BookmarksFragment fragment = new BookmarksFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ToolbarActionsListener) {
            navDrawerListener = (ToolbarActionsListener) context;
        } else {
            throw new RuntimeException(
                    "The parent of this fragment must implement ToolbarActionsListener interface.");
        }

        if (context instanceof QuranNavigationCallbacks) {
            quranNavigationCallbacks = (QuranNavigationCallbacks) context;
        } else {
            throw new RuntimeException(
                    "The parent of this fragment must implement QuranNavigationCallbacks interface.");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        butterknifeUnbinder = ButterKnife.bind(this, view);

        presenter = new BookmarksPresenterImp();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.searchList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bookmarksListFragment = BookmarksListFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.list_container, bookmarksListFragment);
        transaction.commit();

        presenter.onAttach(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDetach();
        butterknifeUnbinder.unbind();
    }

    @OnClick(R.id.hamburger_iv)
    void onNavHamburgerClick() {
        navDrawerListener.onNavDrawerClick();
    }

    @OnClick(R.id.edit_btn)
    void edit() {
        if (isListEditable) {
            presenter.enableEditList();
        } else {
            showMessage(getString(R.string.msg_no_bookmarks));
        }
    }

    @OnClick(R.id.filter_btn)
    void filter() {
        presenter.filterList();
    }

    @OnClick(R.id.ib_finish_edit)
    void finishEdit() {
        presenter.finishEditList();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() { /* there's no need for this here */ }

    @Override
    public void hideLoading() { /* there's no need for this here */ }

    @Override
    public void enableEditList() {
        editButton.setVisibility(View.INVISIBLE);
        finishEditImageButton.setVisibility(View.VISIBLE);
        filterButton.setVisibility(View.INVISIBLE);
        bookmarksListFragment.setEditBookmarks(true);
    }

    @Override
    public void finishEditList() {
        editButton.setVisibility(View.VISIBLE);
        finishEditImageButton.setVisibility(View.INVISIBLE);
        filterButton.setVisibility(View.VISIBLE);
        bookmarksListFragment.setEditBookmarks(false);
    }

    @Override
    public void filterList() {
        bookmarksListFragment.showFilterDialog();
    }

    @Override
    public void searchList(@NonNull String text) {
        bookmarksListFragment.searchBookmarks(text);
    }


    @Override
    public void onEditabilityChange(boolean isEditable) {
        isListEditable = isEditable;

        // disable/enable the edit image button
        if (isEditable) {
            editButton.setImageResource(R.drawable.edit_gold_ic);
            editButton.setColorFilter(ContextCompat.getColor(getActivity(), R.color.color_accent));
        } else {
            editButton.setColorFilter(ContextCompat.getColor(getActivity(), R.color.dark_grey));
        }

    }

    @Override
    public void gotoQuranPage(int pageNumber) {
        quranNavigationCallbacks.gotoQuranPage(pageNumber);
    }

    @Override
    public void gotoQuranPageAya(int pageNumber, int ayaId, boolean addToBackStack) {
        ScreenUtil.dismissKeyboard(requireActivity(), searchEditText);
        quranNavigationCallbacks.gotoQuranPageAya(pageNumber, ayaId, false);
    }

}
