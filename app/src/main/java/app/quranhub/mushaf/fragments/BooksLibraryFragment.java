package app.quranhub.mushaf.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import app.quranhub.R;
import app.quranhub.utils.interfaces.ToolbarActionsListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BooksLibraryFragment extends Fragment {

    private static final String FRAGMENT_BOOKS = "FRAGMENT_BOOKS";
    private static final String FRAGMENT_LIBRARY = "FRAGMENT_LIBRARY";
    private static final String STATE_SELECTED_TAB = "STATE_SELECTED_TAB";
    private static final String STATE_INPUT_SEARCH = "STATE_INPUT_SEARCH";
    private static final String STATE_EDITABLE = "STATE_EDITABLE";

    private static final int LIBRARY_TAB = 0;
    private static final int BOOKS_TAB = 1;

    private int selectedTab = LIBRARY_TAB;
    private BookDataFragment bookDataFragment;
    private LibraryFragment libraryFragment;
    private String inputSearch = "";
    private ToolbarActionsListener navDrawerListener;
    private boolean isEditable = false;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.et_search)
    EditText searchEt;
    @BindView(R.id.edit_btn)
    ImageView editBtn;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ToolbarActionsListener) {
            navDrawerListener = (ToolbarActionsListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books_library, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        restoreSavedInstanceState(savedInstanceState);
        addFragment(selectedTab);
        listenOnSelectedTab();
        observeOnInputSearch();
    }


    private void restoreSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            selectedTab = savedInstanceState.getInt(STATE_SELECTED_TAB);
            inputSearch = savedInstanceState.getString(STATE_INPUT_SEARCH);
            isEditable = savedInstanceState.getBoolean(STATE_EDITABLE);
            if (isEditable) {
                editBtn.setImageResource(R.drawable.check_gold_ic);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_TAB, selectedTab);
        outState.putString(STATE_INPUT_SEARCH, inputSearch);
        outState.putBoolean(STATE_EDITABLE, isEditable);
    }

    private void observeOnInputSearch() {
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputSearch = s.toString();
                if (tabLayout.getSelectedTabPosition() == LIBRARY_TAB && libraryFragment != null) {
                    libraryFragment.search(inputSearch);
                } else if (tabLayout.getSelectedTabPosition() == BOOKS_TAB && bookDataFragment != null) {
                    bookDataFragment.search(inputSearch);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void listenOnSelectedTab() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                addFragment(tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void addFragment(int tab) {
        selectedTab = tab;
        tabLayout.getTabAt(selectedTab).select();
        searchEt.getText().clear();
        inputSearch = "";
        if (tab == LIBRARY_TAB) {
            editBtn.setVisibility(View.INVISIBLE);
            libraryFragment = (LibraryFragment) getChildFragmentManager().findFragmentByTag(FRAGMENT_LIBRARY);
            if (libraryFragment == null) {
                libraryFragment = new LibraryFragment();
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.data_container, libraryFragment, FRAGMENT_LIBRARY)
                        .commit();
            }
        } else if (tab == BOOKS_TAB) {
            editBtn.setVisibility(View.VISIBLE);
            bookDataFragment = (BookDataFragment) getChildFragmentManager().findFragmentByTag(FRAGMENT_BOOKS);
            if (bookDataFragment == null) {
                bookDataFragment = BookDataFragment.getInstance(true);
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.data_container, bookDataFragment, FRAGMENT_BOOKS)
                        .commit();
            }
        }
    }

    @OnClick(R.id.hamburger_iv)
    public void onNavHamburgerClick() {
        navDrawerListener.onNavDrawerClick();
    }

    @OnClick(R.id.edit_btn)
    public void onEditClick() {
        if (isEditable) {
            editBtn.setImageResource(R.drawable.edit_gold_ic);
            bookDataFragment.toggleNormalMode();
        } else {
            editBtn.setImageResource(R.drawable.check_gold_ic);
            bookDataFragment.toggleEditAction();
        }
        isEditable = !isEditable;
    }


}
