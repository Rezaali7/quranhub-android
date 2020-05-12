package app.quranhub.mushaf.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import app.quranhub.R;
import app.quranhub.mushaf.adapter.Guz2IndexAdapter;
import app.quranhub.mushaf.listener.QuranNavigationCallbacks;
import app.quranhub.mushaf.model.HizbQuarterDataModel;
import app.quranhub.mushaf.viewmodel.Guz2IndexViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment that displays a list containing Juz' index with its Hizb & Hizb Quarters.
 */
public class Guz2IndexFragment extends Fragment implements Guz2IndexAdapter.IndexItemClickListener {

    private static final String TAG = Guz2IndexFragment.class.getSimpleName();

    private static final String ARG_FILTER_GUZ2 = "ARG_FILTER_GUZ2";
    private static final String STATE_FILTER_GUZ2 = "STATE_FILTER_GUZ2";

    @BindView(R.id.rv_guz2_index)
    RecyclerView guz2IndexRecyclerView;
    @BindView(R.id.guz2_index_progress_bar)
    ProgressBar progressBar;
    private Unbinder butterknifeUnbinder;

    private QuranNavigationCallbacks quranNavigationCallbacks;

    private Guz2IndexViewModel guz2IndexViewModel;

    private Guz2IndexAdapter adapter;

    private int filterGuz2 = Guz2IndexAdapter.FILTER_GUZ2_ALL;


    public Guz2IndexFragment() {
        // Required empty public constructor
    }

    public static Guz2IndexFragment newInstance(int filterGuz2) {
        Guz2IndexFragment fragment = new Guz2IndexFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FILTER_GUZ2, filterGuz2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof QuranNavigationCallbacks) {
            quranNavigationCallbacks = (QuranNavigationCallbacks) context;
        } else {
            throw new RuntimeException(
                    "The containing Activity must implement QuranNavigationCallbacks interface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filterGuz2 = getArguments().getInt(ARG_FILTER_GUZ2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_guz2_index, container, false);
        butterknifeUnbinder = ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            filterGuz2 = savedInstanceState.getInt(STATE_FILTER_GUZ2, Guz2IndexAdapter.FILTER_GUZ2_ALL);
        }

        initGuz2IndexRecyclerView();

        guz2IndexViewModel = new ViewModelProvider(this).get(Guz2IndexViewModel.class);
        guz2IndexViewModel.getHizbQuarterDataModelsLiveData().observe(getViewLifecycleOwner(), hizbQuarterDataModels -> {
            Log.d(TAG, "hizbQuarterDataModels = " + hizbQuarterDataModels);

            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }
            adapter.setHizbQuarterDataModels(hizbQuarterDataModels);
        });
        guz2IndexViewModel.indexItemClickEvent().observe(getViewLifecycleOwner(),
                indexItemClickEvent -> quranNavigationCallbacks.gotoQuranPage(indexItemClickEvent.page));
    }

    private void initGuz2IndexRecyclerView() {
        guz2IndexRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        guz2IndexRecyclerView.setLayoutManager(layoutManager);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(),
                layoutManager.getOrientation());
        guz2IndexRecyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new Guz2IndexAdapter(null, filterGuz2, this);
        guz2IndexRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_FILTER_GUZ2, filterGuz2);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        butterknifeUnbinder.unbind();
    }

    @Override
    public void onIndexItemClick(HizbQuarterDataModel model, int clickedItemIndex) {
        guz2IndexViewModel.notifyIndexItemClick(clickedItemIndex);
    }

    public void filterForGuz2(int guz2) {
        filterGuz2 = guz2;
        adapter.getFilter().filter(Integer.toString(guz2));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        quranNavigationCallbacks = null;
    }
}
