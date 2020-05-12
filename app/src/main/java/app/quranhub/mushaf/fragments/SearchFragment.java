package app.quranhub.mushaf.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import app.quranhub.mushaf.adapter.SearchAdapter;
import app.quranhub.mushaf.dialogs.OptionDialog;
import app.quranhub.mushaf.listener.ItemSelectionListener;
import app.quranhub.mushaf.listener.QuranNavigationCallbacks;
import app.quranhub.mushaf.model.SearchModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import app.quranhub.R;
import app.quranhub.mushaf.viewmodel.SearchViewModel;
import app.quranhub.settings.dialogs.OptionsListDialogFragment;
import app.quranhub.utils.ScreenUtil;
import app.quranhub.utils.interfaces.ToolbarActionsListener;


public class SearchFragment extends Fragment implements ItemSelectionListener<SearchModel>, OptionDialog.ItemClickListener, OptionsListDialogFragment.ItemSelectionListener {


    @BindView(R.id.search_rv)
    RecyclerView searchRv;
    @BindView(R.id.et_search)
    EditText searchEt;
    @BindView(R.id.ib_clear_search)
    ImageButton clearSearchImageButton;
    @BindView(R.id.noresult_tv)
    TextView noResultTv;
    @BindView(R.id.sura_tv)
    TextView suraTv;
    @BindView(R.id.chapter_tv)
    TextView chapterTv;
    @BindView(R.id.rob3_tv)
    TextView quarterTv;
    @BindView(R.id.hezb_tv)
    TextView hezbTv;
    @BindView(R.id.progrees_bar)
    ProgressBar progressBar;
    @BindView(R.id.filter_container)
    ConstraintLayout filterContainer;

    public static final int SURA_FILTER_CODE = 1;
    public static final int JUZ_FILTER_CODE = 2;
    public static final int HEZB_FILTER_CODE = 3;
    public static final int QUARTER_FILTER_CODE = 4;
    private boolean isOriented = false, isFilterOptionsShow = false;
    private QuranNavigationCallbacks quranNavigationCallbacks;
    private String inputSearch = "";
    private ToolbarActionsListener navDrawerListener;
    private SearchAdapter searchAdapter;
    private SearchViewModel searchViewModel;
    private int selectedSura = 0;
    private int selectedJuz = 0;
    private int selectedHezb = 0;
    private int selectedQuarter = 0;
    private String option;
    private List<String> suraOptions;
    private List<String> juzOptions;
    private List<String> hezbOptions;
    private List<String> quarterOptions;
    private List<Integer> juzSuraNumbers;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ToolbarActionsListener) {
            navDrawerListener = (ToolbarActionsListener) context;
        }
        if (context instanceof QuranNavigationCallbacks) {
            quranNavigationCallbacks = (QuranNavigationCallbacks) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_search, container, false);

        View view = getView() != null ? getView() :
                inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            isOriented = true;
            getPrevState(savedInstanceState);
        }

        initRecycler();
        bindViewModel();
        observeOnInputSearch();
        setViewsFromBackStack();
    }

    private void setViewsFromBackStack() {
        if(isFilterOptionsShow) {
            filterContainer.setVisibility(View.VISIBLE);
        }
        if(selectedSura != 0) {
            suraTv.setText(getActivity().getResources().getStringArray(R.array.sura_name)[selectedSura - 1]);
        }
        if(selectedJuz != 0) {
            chapterTv.setText(refactorOptionText(getActivity().getResources().getStringArray(R.array.agza2_name)[selectedJuz - 1]));
        }
        if(selectedHezb != 0) {
            hezbTv.setText(hezbOptions.get(selectedHezb));
        }
        if(selectedQuarter != 0) {
            quarterTv.setText(quarterOptions.get(selectedQuarter));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("input_search", inputSearch);
        outState.putInt("selected_juz", selectedJuz);
        outState.putInt("selected_sura", selectedSura);
        outState.putInt("input_hezb", selectedHezb);
        outState.putInt("input_qurater", selectedQuarter);
    }

    @SuppressLint("CheckResult")
    private void observeOnInputSearch() {

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputSearch = s.toString();
                if (!isOriented) {
                    progressBar.setVisibility(View.VISIBLE);
                    searchAya();
                } else {
                    isOriented = false;
                }

                // show or hide clear button in search field
                if (TextUtils.isEmpty(s)) {
                    clearSearchImageButton.setVisibility(View.INVISIBLE);
                }
                else {
                    clearSearchImageButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @OnClick(R.id.ib_clear_search)
    void clearSearch() {
        searchEt.getText().clear();
    }

    private void searchAya() {
        if (inputSearch.trim().isEmpty()) {
            clearResult();
        } else if (selectedJuz != 0 && selectedHezb != 0 && selectedQuarter != 0) {
            searchViewModel.searchWithSuraAndJuzAndHizbQuarter(inputSearch, selectedSura, selectedJuz, selectedHezb, selectedQuarter);
        } else if (selectedJuz != 0 && selectedHezb != 0) {
            searchViewModel.searchWithSuraAndJuzAndHizb(inputSearch, selectedSura, selectedJuz, selectedHezb);
        } else if (selectedSura != 0 && selectedJuz != 0) {
            searchViewModel.searchWithSuraAndJuz(inputSearch, selectedSura, selectedJuz);
        } else if (selectedSura != 0) {
            searchViewModel.searchWithSura(inputSearch, selectedSura);
        } else if (selectedJuz != 0) {
            searchViewModel.searchWithJuz(inputSearch, selectedJuz);
        } else {
            searchViewModel.simpleSearch(inputSearch);
        }
    }

    private void clearResult() {
        searchAdapter.setSearchModels(new ArrayList<>());
        noResultTv.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void bindViewModel() {
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        searchViewModel.getSearch().observe(getViewLifecycleOwner(), searchModels -> {
            progressBar.setVisibility(View.GONE);
            if (searchModels == null) {
                Toast.makeText(getActivity(), getString(R.string.search_failed), Toast.LENGTH_LONG).show();
            } else if (searchModels.isEmpty()) {
                clearResult();
            } else if (!inputSearch.trim().isEmpty()) {
                noResultTv.setVisibility(View.GONE);
                searchAdapter.setSearchModels(searchModels);
            }
        });

        searchViewModel.getSura().observe(getViewLifecycleOwner(), results -> {
            suraOptions = new ArrayList<>();
            juzSuraNumbers = results;
            getJuzSuras();
        });
    }

    private void getJuzSuras() {
        List<String> surahs = Arrays.asList(getResources().getStringArray(R.array.sura_name));
        suraOptions = new ArrayList<>();
        for (int index : juzSuraNumbers) {
            suraOptions.add(surahs.get(index - 1));
        }
    }

    private void initRecycler() {
        searchAdapter = new SearchAdapter(getActivity(), this);
        searchRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchRv.setAdapter(searchAdapter);
    }

    private void getPrevState(Bundle savedInstanceState) {
        inputSearch = savedInstanceState.getString("input_search");
        selectedJuz = savedInstanceState.getInt("selected_juz");
        selectedSura = savedInstanceState.getInt("selected_sura");
        selectedHezb = savedInstanceState.getInt("input_hezb");
        selectedQuarter = savedInstanceState.getInt("input_qurater");
        if (selectedJuz != 0) {
            chapterTv.setText(refactorOptionText(getActivity().getResources().getStringArray(R.array.agza2_name)[selectedJuz - 1]));
        }
        if (selectedSura != 0) {
            suraTv.setText(getActivity().getResources().getStringArray(R.array.sura_name)[selectedSura - 1]);
        }
        if (selectedHezb != 0) {
            hezbTv.setText(getActivity().getResources().getStringArray(R.array.hezb_name)[selectedHezb - 1]);
        }
        if (selectedQuarter != 0) {
            quarterTv.setText(getActivity().getResources().getStringArray(R.array.quarter_name)[selectedQuarter - 1]);
        }
    }

    @OnClick(R.id.hamburger_iv)
    public void onNavHamburgerClick() {
        navDrawerListener.onNavDrawerClick();
    }

    @OnClick(R.id.part_container)
    public void onClickPartFilter() {
        if (juzOptions == null) {
            List<String> options = Arrays.asList(getResources().getStringArray(R.array.agza2_name));
            juzOptions = new ArrayList<>();
            juzOptions.add(getString(R.string.all_guz2));
            juzOptions.addAll(options);
        }
        if (selectedJuz == 0) {
            option = getString(R.string.all_guz2);
        } else {
            option = Objects.requireNonNull(getActivity()).getResources().getStringArray(R.array.agza2_name)[selectedJuz - 1];
        }
        DialogFragment dialog = OptionDialog.getInstance(juzOptions, option, JUZ_FILTER_CODE, getString(R.string.chapters));
        dialog.show(getChildFragmentManager(), "JuzDialog");
    }

    private void setSuraDialog() {
        if (selectedSura == 0) {
            option = getString(R.string.all_sura);
        } else {
            option = Objects.requireNonNull(getActivity()).getResources().getStringArray(R.array.sura_name)[selectedSura - 1];
        }
        DialogFragment dialog = OptionDialog.getInstance(suraOptions, option, SURA_FILTER_CODE, getString(R.string.suras));
        dialog.show(getChildFragmentManager(), "OptionDialog");
    }

    @OnClick(R.id.sura_container)
    public void onClickSuraFilter() {
        if (selectedJuz == 0) {
            List<String> options = Arrays.asList(getResources().getStringArray(R.array.sura_name));
            suraOptions = new ArrayList<>();
            suraOptions.add(getString(R.string.all_sura));
            suraOptions.addAll(options);
        }
        setSuraDialog();
    }

    @OnClick(R.id.hezb_container)
    public void onClickHezbFilter() {
        if (selectedJuz == 0) {
            Toast.makeText(getActivity(), R.string.select_juz_first, Toast.LENGTH_LONG).show();
            return;
        }
        if (hezbOptions == null) {
            List<String> options = Arrays.asList(getResources().getStringArray(R.array.hezb_name));
            hezbOptions = new ArrayList<>();
            hezbOptions.add(getString(R.string.all_hezb));
            hezbOptions.addAll(options);
        }
        OptionsListDialogFragment fragment = OptionsListDialogFragment.getInstance(getString(R.string.hizb), hezbOptions, selectedHezb, this, HEZB_FILTER_CODE);
        fragment.show(getActivity().getSupportFragmentManager(), "HizbFilterDialog");
    }

    @OnClick(R.id.rob3_container)
    public void onClickQuraterFilter() {
        if (selectedHezb == 0) {
            Toast.makeText(getActivity(), R.string.select_hezb_first, Toast.LENGTH_LONG).show();
            return;
        }
        if (quarterOptions == null) {
            List<String> options = Arrays.asList(getResources().getStringArray(R.array.quarter_name));
            quarterOptions = new ArrayList<>();
            quarterOptions.add(getString(R.string.all_quarters));
            quarterOptions.addAll(options);
        }
        OptionsListDialogFragment fragment = OptionsListDialogFragment.getInstance(getString(R.string.rub3), quarterOptions, selectedQuarter, this, QUARTER_FILTER_CODE);
        fragment.show(getActivity().getSupportFragmentManager(), "QuarterFilterDialog");
    }

    @Override
    public void onSelectItem(SearchModel item) {
        ScreenUtil.dismissKeyboard(getContext(), searchEt);
        quranNavigationCallbacks.gotoQuranPageAya(item.getPage(), item.getId(), true);
    }

    @Override
    public void onItemClick(String optionName, int optionIndex, int requestCode) {
        if (requestCode == SURA_FILTER_CODE) {
            suraTv.setText(optionName);
            selectedSura = selectedJuz == 0 ? optionIndex : juzSuraNumbers.get(optionIndex);
            searchAya();
        } else if (requestCode == JUZ_FILTER_CODE) {
            chapterTv.setText(optionIndex == 0 ? optionName : refactorOptionText(optionName));
            suraTv.setText(getString(R.string.sura));
            selectedSura = 0;
            if (optionIndex == 0) {
                selectedQuarter = 0;
                selectedHezb = 0;
                quarterTv.setText(getString(R.string.rub3));
                hezbTv.setText(getString(R.string.hizb));
            } else {
                searchViewModel.getChapterSuras(optionIndex);
            }
            if (optionIndex != selectedJuz) {
                selectedJuz = optionIndex;
                searchAya();
            }
        }
    }

    private String refactorOptionText(String text) {
        return text.substring(text.indexOf(' ') + 1);
    }


    @Override
    public void onItemSelected(int requestCode, int itemIndex) {
        if (requestCode == HEZB_FILTER_CODE) {
            selectedHezb = itemIndex;
            hezbTv.setText(hezbOptions.get(itemIndex));
            if (selectedHezb == 0) {
                selectedQuarter = 0;
                quarterTv.setText(getString(R.string.rub3));
            }
            searchAya();
        } else if (requestCode == QUARTER_FILTER_CODE) {
            selectedQuarter = itemIndex;
            quarterTv.setText(quarterOptions.get(itemIndex));
            searchAya();
        }
    }

    @OnClick(R.id.more_iv)
    public void onGetMoreFilterOptions() {
        if (isFilterOptionsShow) {
            filterContainer.setVisibility(View.GONE);
        } else {
            filterContainer.setVisibility(View.VISIBLE);
        }

        isFilterOptionsShow = !isFilterOptionsShow;
    }
}
