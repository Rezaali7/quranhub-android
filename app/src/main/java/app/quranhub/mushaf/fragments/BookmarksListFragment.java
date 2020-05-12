package app.quranhub.mushaf.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.quranhub.R;
import app.quranhub.mushaf.adapter.BookmarksAdapter;
import app.quranhub.mushaf.data.entity.BookmarkType;
import app.quranhub.mushaf.dialogs.BookmarkEditDialog;
import app.quranhub.mushaf.listener.BookmarksListListener;
import app.quranhub.mushaf.listener.QuranNavigationCallbacks;
import app.quranhub.mushaf.model.DisplayableBookmark;
import app.quranhub.mushaf.viewmodel.BookmarksListViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A fragment representing a list of user saved bookmarked Quran ayas.
 * Use the {@link BookmarksListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookmarksListFragment extends Fragment
        implements BookmarksAdapter.BookmarkActionListener, BookmarkEditDialog.BookmarkFilterListener {

    private static final String TAG = BookmarksListFragment.class.getSimpleName();

    private BookmarksListViewModel bookMarksViewModel;
    private DialogFragment bookmarkFilterDialog;
    private BookmarksListListener bookmarksListener;
    private List<BookmarkType> bookmarkTypes;
    private int editedBookmarkId = -1;
    @BindView(R.id.bookmarks_rv)
    RecyclerView bookmarksRecyclerView;
    @BindView(R.id.loading_indicator)
    ProgressBar progressBar;
    @BindView(R.id.tv_empty_list_msg)
    TextView emptyListMsgTextView;

    private Unbinder butterknifeUnbinder;

    private BookmarksAdapter adapter;
    private int selectedFilterType = BookmarkEditDialog.ALL_BOOKMARK_FILTER;

    private QuranNavigationCallbacks quranNavigationCallbacks;


    public BookmarksListFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment {@link BookmarksListFragment}.
     */
    public static BookmarksListFragment newInstance() {
        BookmarksListFragment fragment = new BookmarksListFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getParentFragment() instanceof QuranNavigationCallbacks
                && getParentFragment() instanceof BookmarksListListener) {
            quranNavigationCallbacks = (QuranNavigationCallbacks) getParentFragment();
            bookmarksListener = (BookmarksListListener) getParentFragment();
        } else {
            throw new RuntimeException(
                    getParentFragment().getClass().getSimpleName() + " must implement " +
                            "QuranNavigationCallbacks & BookmarksListener interfaces.");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bookmarks_list, container, false);
        butterknifeUnbinder = ButterKnife.bind(this, view);

        setupBookmarksRecyclerView();
        bookmarkFilterDialog = new BookmarkEditDialog();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindViewModel();
    }

    private void bindViewModel() {
        bookMarksViewModel = new ViewModelProvider(this).get(BookmarksListViewModel.class);
        bookMarksViewModel.getBookmarkTypes();
        bookMarksViewModel.getBookmarks().observe(getViewLifecycleOwner(), ayaBookmarks -> {

            bookmarksListener.onEditabilityChange(ayaBookmarks.size() > 0);

            bookMarksViewModel.bookmarksMapper(ayaBookmarks, displayableBookmarks -> {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                }
                adapter.setBookmarks(displayableBookmarks);
                if (displayableBookmarks.size() > 0) {
                    emptyListMsgTextView.setVisibility(View.GONE);
                } else {
                    emptyListMsgTextView.setVisibility(View.VISIBLE);
                }
            });
        });

        bookMarksViewModel.getBookmarksTypes().observe(getViewLifecycleOwner(), bookmarkTypes -> {
            progressBar.setVisibility(View.GONE);
            this.bookmarkTypes = bookmarkTypes;
        });
    }

    private void setupBookmarksRecyclerView() {
        bookmarksRecyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        bookmarksRecyclerView.setHasFixedSize(true);
        bookmarksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookmarksAdapter(getContext(), this);
        bookmarksRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        butterknifeUnbinder.unbind();
    }

    @Override
    public void onSelectItem(DisplayableBookmark displayableBookmark) {
        quranNavigationCallbacks.gotoQuranPageAya(displayableBookmark.getPageNumber(), displayableBookmark.getAyaId(), false);
    }

    public void setEditBookmarks(boolean isEditable) {
        adapter.setEditable(isEditable);
    }


    public void showFilterDialog() {
        if (bookmarkTypes != null) {
            BookmarkEditDialog dialog = BookmarkEditDialog.getInstance(bookmarkTypes, selectedFilterType, false);
            dialog.show(getChildFragmentManager(), "BookmarkEditDialog");
        }
    }

    public void searchBookmarks(String text) {
        adapter.getFilter().filter(text);
    }

    @Override
    public void deleteBookmark(@NonNull DisplayableBookmark displayableBookmark) {
        bookMarksViewModel.deleteBookmark(displayableBookmark.getBookmarkId());
        adapter.deleteBookmark(displayableBookmark.getAyaId());
    }

    @Override
    public void updateBookmarkType(@NonNull int bookmarkId) {
        if (bookmarkTypes != null) {
            editedBookmarkId = bookmarkId;
            BookmarkEditDialog dialog = BookmarkEditDialog.getInstance(bookmarkTypes, bookmarkId, true);
            dialog.show(getChildFragmentManager(), "BookmarkEditDialog");
        }
    }

    @Override
    public void onBookmarkFilter(int bookmarkType, int colorIndex) {
        if (editedBookmarkId == -1) {
            selectedFilterType = bookmarkType;
            adapter.filterBookmarks(bookmarkType);
        } else {
            bookMarksViewModel.changeBookmarkType(editedBookmarkId, bookmarkType);
            adapter.editBookmark(editedBookmarkId, bookmarkType, colorIndex);
            editedBookmarkId = -1;
        }
    }


}
