package app.quranhub.mushaf.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import app.quranhub.R;
import app.quranhub.utils.interfaces.ToolbarActionsListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuranToolbarFragment extends Fragment {

    public static final int PAGE_DIR_RIGHT = 0;
    public static final int PAGE_DIR_LEFT = 1;

    @BindView(R.id.ll_root)
    LinearLayout rootLinearLayout;
    @BindView(R.id.page_guz2_tv)
    TextView pageGuz2Tv;
    @BindView(R.id.page_sura_tv)
    TextView pageSuraTv;
    @BindView(R.id.iv_page_dir)
    ImageView pageDirImageView;

    private ToolbarActionsListener toolbarActionsListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ToolbarActionsListener) {
            toolbarActionsListener = (ToolbarActionsListener) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quran_toolbar, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        pageSuraTv.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/diwany_thuluth.ttf"));
        pageGuz2Tv.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/diwany_thuluth.ttf"));
        rootLinearLayout.setOnTouchListener((v, event) -> {
            return true; // To prevent event bubbling to the views below this one
        });
    }

    @OnClick(R.id.hamburger_iv)
    public void onNavHamburgerClick() {
        toolbarActionsListener.onNavDrawerClick();
    }

    @OnClick(R.id.page_guz2_tv)
    void onGuz2Click() {
        toolbarActionsListener.onGuz2Click();
    }

    @OnClick(R.id.page_sura_tv)
    void onSuraClick() {
        toolbarActionsListener.onSuraClick();
    }

    @OnClick(R.id.ic_bookmark)
    void onClickBookmark() {
        toolbarActionsListener.onBookmarkClick();
    }

    public void setSuraText(String suraName) {
        pageSuraTv.setText(suraName);
    }

    public void setGuz2Text(String currentGuz2) {
        pageGuz2Tv.setText(currentGuz2);
    }

    /**
     * Control page dir icon.
     *
     * @param pageDir either {@link #PAGE_DIR_LEFT} or {@link #PAGE_DIR_RIGHT}.
     */
    public void setPageDir(int pageDir) {
        switch (pageDir) {
            case PAGE_DIR_RIGHT:
                pageDirImageView.setImageResource(R.drawable.quran_page_right);
                break;
            case PAGE_DIR_LEFT:
                pageDirImageView.setImageResource(R.drawable.quran_page_left);
                break;
            default:
                throw new IllegalArgumentException("Invalid page dir.");
        }

    }

}
