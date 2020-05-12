package app.quranhub.mushaf.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.quranhub.mushaf.adapter.SuraIndexAdapter;
import app.quranhub.mushaf.listener.ItemSelectionListener;
import app.quranhub.mushaf.model.SuraIndexModelMapper;
import butterknife.BindView;
import butterknife.ButterKnife;
import app.quranhub.R;


public class SuraIndexFragment extends Fragment implements ItemSelectionListener<Integer> {


    @BindView(R.id.sura_index_rv)
    RecyclerView suraIndexRv;

    private SuraIndexAdapter adapter;

    private ViewPropertyAnimator fastScrollerAnimator;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sura_index, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRecycler();
    }

    private void initRecycler() {
        suraIndexRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SuraIndexAdapter(getActivity(), this);
        suraIndexRv.setAdapter(adapter);
    }

    public void onSearchSura(String inputQuery) {
        adapter.filter(inputQuery);
    }

    @Override
    public void onSelectItem(Integer suraPage) {
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof SuraGuz2IndexFragment) {
            ((SuraGuz2IndexFragment) parentFragment).navigateToSelectedSura(suraPage);
        }
    }

    public void setAdapterData(List<SuraIndexModelMapper> indexList) {
        adapter.setSuraIndexModelList(indexList);
        suraIndexRv.getRecycledViewPool().clear();
    }


}
