package app.quranhub.mushaf.fragments;


import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.quranhub.R;
import app.quranhub.mushaf.adapter.Guz2IndexAdapter;
import app.quranhub.mushaf.listener.QuranNavigationCallbacks;
import app.quranhub.mushaf.model.SuraIndexModelMapper;
import app.quranhub.mushaf.presenter.SuraGuz2IndexPresenter;
import app.quranhub.mushaf.presenter.SuraGuz2IndexPresenterImp;
import app.quranhub.mushaf.view.SuraGuz2IndexView;
import app.quranhub.settings.dialogs.OptionsListDialogFragment;
import app.quranhub.utils.ScreenUtil;
import app.quranhub.utils.interfaces.ToolbarActionsListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class SuraGuz2IndexFragment extends Fragment implements SuraGuz2IndexView, OptionsListDialogFragment.ItemSelectionListener {

    private static final String TAG = SuraGuz2IndexFragment.class.getSimpleName();


    private static final String ARG_SELECTED_TAB = "ARG_SELECTED_TAB";

    private static final String STATE_SELECTED_TAB = "STATE_SELECTED_TAB";
    private static final String STATE_INPUT_SEARCH = "STATE_INPUT_SEARCH";
    private static final String STATE_SELECTED_GUZ2_FILTER = "STATE_SELECTED_GUZ2_FILTER";

    private static final String FRAGMENT_SURA_INDEX = "FRAGMENT_SURA_INDEX";
    private static final String FRAGMENT_GUZ2_INDEX = "FRAGMENT_GUZ2_INDEX";

    public static final int SURA_INDEX_TAB = 0;
    public static final int GUZ2_INDEX_TAB = 1;
    private static final int RC_GUZ2_FILTER = 0;

    private ToolbarActionsListener toolbarActionsListener;
    private QuranNavigationCallbacks quranNavigationCallbacks;

    private int selectedTab = SURA_INDEX_TAB;
    private SuraIndexFragment suraIndexFragment;
    private Guz2IndexFragment guz2IndexFragment;
    private SuraGuz2IndexPresenter presenter;
    private String inputSearch = "";
    private int selectedGUZ2Filter = Guz2IndexAdapter.FILTER_GUZ2_ALL;


    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.filter_btn)
    ImageView filterBtn;
    @BindView(R.id.et_search)
    EditText searchEt;
    @BindView(R.id.progrees_bar)
    ProgressBar progressBar;

    private Unbinder butterknifeUnbinder;


    public static SuraGuz2IndexFragment newInstance(int selectedTab) {
        SuraGuz2IndexFragment fragment = new SuraGuz2IndexFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SELECTED_TAB, selectedTab);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ToolbarActionsListener && context instanceof QuranNavigationCallbacks) {
            toolbarActionsListener = (ToolbarActionsListener) context;
            quranNavigationCallbacks = (QuranNavigationCallbacks) context;
        } else {
            throw new RuntimeException("The parent activity must implement ToolbarActionsListener" +
                    " & QuranNavigationCallbacks interfaces.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedTab = getArguments().getInt(ARG_SELECTED_TAB);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sura_guz2_index, container, false);
        butterknifeUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        restoreSavedInstanceState(savedInstanceState);
        initPresenter();
        addIndexFragment(selectedTab);
        listenOnSelectedTab();
        observeOnInputSearch();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        butterknifeUnbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        toolbarActionsListener = null;
        quranNavigationCallbacks = null;
    }

    private void initPresenter() {
        presenter = new SuraGuz2IndexPresenterImp<SuraGuz2IndexView>(getActivity());
        presenter.onAttach(this);
    }

    private void restoreSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            selectedTab = savedInstanceState.getInt(STATE_SELECTED_TAB);
            inputSearch = savedInstanceState.getString(STATE_INPUT_SEARCH);
            selectedGUZ2Filter = savedInstanceState.getInt(STATE_SELECTED_GUZ2_FILTER);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_TAB, selectedTab);
        outState.putString(STATE_INPUT_SEARCH, inputSearch);
        outState.putInt(STATE_SELECTED_GUZ2_FILTER, selectedGUZ2Filter);
    }

    private void observeOnInputSearch() {
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tabLayout.getSelectedTabPosition() == SURA_INDEX_TAB && suraIndexFragment != null) {
                    inputSearch = s.toString();
                    suraIndexFragment.onSearchSura(inputSearch);
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
                addIndexFragment(tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void addIndexFragment(int tab) {
        selectedTab = tab;
        tabLayout.getTabAt(selectedTab).select();
        if (tab == SURA_INDEX_TAB) {
            suraIndexFragment = (SuraIndexFragment) getChildFragmentManager().findFragmentByTag(FRAGMENT_SURA_INDEX);
            if (suraIndexFragment == null) {
                suraIndexFragment = new SuraIndexFragment();
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.index_container, suraIndexFragment, FRAGMENT_SURA_INDEX)
                        .commit();
            }
            filterBtn.setVisibility(View.INVISIBLE);
            searchEt.setVisibility(View.VISIBLE);
            presenter.getSuraIndex();
        } else if (tab == GUZ2_INDEX_TAB) {
            guz2IndexFragment = (Guz2IndexFragment) getChildFragmentManager().findFragmentByTag(FRAGMENT_GUZ2_INDEX);
            if (guz2IndexFragment == null) {
                guz2IndexFragment = Guz2IndexFragment.newInstance(selectedGUZ2Filter);
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.index_container, guz2IndexFragment, FRAGMENT_GUZ2_INDEX)
                        .commit();
            }
            filterBtn.setVisibility(View.VISIBLE);
            searchEt.getText().clear();
            inputSearch = "";
            searchEt.setVisibility(View.GONE);
        }
    }


    @OnClick(R.id.hamburger_iv)
    public void onNavHamburgerClick() {
        toolbarActionsListener.onNavDrawerClick();
    }

    @OnClick(R.id.filter_btn)
    public void onFilterButtonClick() {
        if (tabLayout.getSelectedTabPosition() == GUZ2_INDEX_TAB && guz2IndexFragment != null) {
            List<String> guz2Options = new ArrayList<>();
            guz2Options.add(getString(R.string.all_guz2));
            guz2Options.addAll(Arrays.asList(getResources().getStringArray(R.array.agza2_name)));
            OptionsListDialogFragment guz2Dialog = OptionsListDialogFragment.getInstance(
                    getString(R.string.title_options_dialog_filter_guz2_index),
                    guz2Options, selectedGUZ2Filter, this, RC_GUZ2_FILTER);
            guz2Dialog.show(getFragmentManager(), "guz2Dialog");
        }
    }

    public void navigateToSelectedSura(int suraPage) {
        ScreenUtil.dismissKeyboard(getActivity(), searchEt);
        quranNavigationCallbacks.gotoQuranPage(suraPage);
    }

    @Override
    public void onGetIndex(List<SuraIndexModelMapper> indexList) {
        suraIndexFragment.setAdapterData(indexList);
        if (!TextUtils.isEmpty(inputSearch)) {
            suraIndexFragment.onSearchSura(inputSearch);
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onItemSelected(int requestCode, int itemIndex) { // filter dialog callback
        if (requestCode == RC_GUZ2_FILTER && guz2IndexFragment != null) {
            selectedGUZ2Filter = itemIndex;
            guz2IndexFragment.filterForGuz2(selectedGUZ2Filter);
        }
    }
}
