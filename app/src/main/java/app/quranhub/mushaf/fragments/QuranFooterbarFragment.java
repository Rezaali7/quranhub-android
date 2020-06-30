package app.quranhub.mushaf.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import app.quranhub.R;
import app.quranhub.mushaf.presenter.QuranFooterPresenter;
import app.quranhub.mushaf.presenter.QuranFooterPresenterImp;
import app.quranhub.mushaf.view.QuranFooterView;
import app.quranhub.utils.PreferencesUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class QuranFooterbarFragment extends Fragment implements QuranFooterView {

    private static final String TAG = QuranFooterbarFragment.class.getSimpleName();

    private QuranFooterPresenter presenter;

    private boolean nightMode;

    @BindView(R.id.ll_root)
    LinearLayout rootLinearLayout;
    @BindView(R.id.quran_page_tv)
    TextView quranPageTv;
    @BindView(R.id.quran_night_mode_ib)
    ImageButton nightModeImageButton;
    @BindView(R.id.quran_search_ib)
    ImageButton searchImageButton;
    private Unbinder butterknifeUnbinder;

    private QuranFooterCallbacks footerCallbacks;

    private MutableLiveData<String> pageNumTextLiveData;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (getParentFragment() instanceof QuranFooterCallbacks) {
            footerCallbacks = (QuranFooterCallbacks) getParentFragment();
        } else {
            throw new ClassCastException(
                    "Cannot cast the parent fragment to QuranFooterCallbacks instance.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pageNumTextLiveData = new MutableLiveData<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quran_footerbar, container, false);
        butterknifeUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        setupButtonsTooltips();

        presenter = new QuranFooterPresenterImp();

        nightMode = PreferencesUtils.getNightModeSetting(requireActivity());
        setupNightModeButton();

        rootLinearLayout.setOnTouchListener((v, event) -> {
            return true; // To prevent event bubbling to the views below this one
        });
    }

    private void setupButtonsTooltips() {
        TooltipCompat.setTooltipText(nightModeImageButton, getString(R.string.tooltip_quran_night_mode));
        TooltipCompat.setTooltipText(searchImageButton, getString(R.string.tooltip_quran_search));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        presenter.onAttach(this);

        pageNumTextLiveData.observe(getViewLifecycleOwner(), pageNumText -> {
            quranPageTv.setText(pageNumText);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        butterknifeUnbinder.unbind();
        presenter.onDetach();
    }

    @OnClick(R.id.quran_search_ib)
    void onQuranSearchClick() {
        presenter.displaySearchDialog();
    }

    @OnClick(R.id.quran_night_mode_ib)
    void onQuranNightModeClick() {
        presenter.toggleNightMode();
    }

    private void setupNightModeButton() {
        nightModeImageButton.setImageResource(
                nightMode ? R.drawable.ic_nightmode_on : R.drawable.ic_nightmode_off);
    }

    public void setCurrentPage(String pageNumText) {
        pageNumTextLiveData.setValue(pageNumText);
    }

    @Override
    public void displaySearchDialog() {
        footerCallbacks.openSearchFragment();
    }

    @Override
    public void toggleNightMode() {
        nightMode = footerCallbacks.toggleNightMode();
        setupNightModeButton();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
    }


    public interface QuranFooterCallbacks {
        void openSearchFragment();

        boolean toggleNightMode();
    }

}
