package app.quranhub.mushaf.fragments;


import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.quranhub.R;
import app.quranhub.main.MainActivity;
import app.quranhub.mushaf.adapter.SubjectsAdapter;
import app.quranhub.mushaf.listener.ItemSelectionListener;
import app.quranhub.mushaf.model.TopicCategory;
import app.quranhub.mushaf.model.TopicModel;
import app.quranhub.mushaf.viewmodel.SubjectsViewModel;
import app.quranhub.utils.interfaces.ToolbarActionsListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SubjectsFragment extends Fragment implements ItemSelectionListener<TopicCategory> {

    @BindView(R.id.et_search)
    EditText searchEt;
    @BindView(R.id.topics_rv)
    RecyclerView topicsRv;
    @BindView(R.id.progrees_bar)
    ProgressBar progressBar;

    private SubjectsAdapter adapter;
    private SubjectsViewModel viewModel;
    private ToolbarActionsListener navDrawerListener;
    private List<TopicModel> topicModels;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ToolbarActionsListener) {
            navDrawerListener = (ToolbarActionsListener) context;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subjects, container, false);
        ButterKnife.bind(this, view);
        intiRecycler();
        bindViewModel();
        observeOnInputSearch();
        return view;
    }

    private void observeOnInputSearch() {
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void filter(String inputQuery) {
        if (inputQuery.isEmpty()) {
            adapter = new SubjectsAdapter(topicModels, this);
            topicsRv.setAdapter(adapter);
        } else {
            List<TopicModel> filteredList = new ArrayList<>();
            for (TopicModel row : topicModels) {
                if (row.getTopicName().toLowerCase().contains(inputQuery.toLowerCase())) {
                    filteredList.add(row);
                }
            }
            adapter = new SubjectsAdapter(filteredList, this);
            topicsRv.setAdapter(adapter);
        }
    }

    private void bindViewModel() {
        List<String> subjects = Arrays.asList(getActivity().getResources().getStringArray(R.array.subject_name));
        List<String> subjectsCategory = Arrays.asList(getActivity().getResources().getStringArray(R.array.subject_category_name));
        viewModel = ViewModelProviders.of(this).get(SubjectsViewModel.class);
        viewModel.getSubjects(subjects, subjectsCategory);
        viewModel.getSubjectsLiveData().observe(getViewLifecycleOwner(), topicModels -> {
            progressBar.setVisibility(View.GONE);
            this.topicModels = topicModels;
            adapter = new SubjectsAdapter(topicModels, this);
            topicsRv.setAdapter(adapter);
        });
    }

    private void intiRecycler() {
        topicModels = new ArrayList<>();
        topicsRv.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onSelectItem(TopicCategory category) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.openTopicAyasFragment(category);
        }
    }

    @OnClick(R.id.hamburger_iv)
    public void onNavHamburgerClick() {
        navDrawerListener.onNavDrawerClick();
    }
}
