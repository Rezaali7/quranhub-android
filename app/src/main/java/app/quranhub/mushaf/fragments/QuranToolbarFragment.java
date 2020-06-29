package app.quranhub.mushaf.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TooltipCompat;
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
    @BindView(R.id.btn_page_guz2)
    Button pageGuz2Button;
    @BindView(R.id.btn_page_sura)
    Button pageSuraButton;
    @BindView(R.id.iv_page_dir)
    ImageView pageDirImageView;

    private ToolbarActionsListener toolbarActionsListener;


    @Override
    public void onAttach(@NonNull Context context) {
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
        TooltipCompat.setTooltipText(pageDirImageView, getText(R.string.tooltip_page_dir));

        rootLinearLayout.setOnTouchListener((v, event) -> {
            return true; // To prevent event bubbling to the views below this one
        });
    }

    @OnClick(R.id.iv_menu)
    public void onNavHamburgerClick() {
        toolbarActionsListener.onNavDrawerClick();
    }

    @OnClick(R.id.btn_page_guz2)
    void onGuz2Click() {
        toolbarActionsListener.onGuz2Click();
    }

    @OnClick(R.id.btn_page_sura)
    void onSuraClick() {
        toolbarActionsListener.onSuraClick();
    }

    public void setSuraText(String suraName) {
        pageSuraButton.setText(suraName);
    }

    public void setGuz2Text(String currentGuz2) {
        pageGuz2Button.setText(currentGuz2);
    }

    /**
     * Control page dir icon.
     *
     * @param pageDir either {@link #PAGE_DIR_LEFT} or {@link #PAGE_DIR_RIGHT}.
     */
    public void setPageDir(int pageDir) {
        switch (pageDir) {
            case PAGE_DIR_RIGHT:
                pageDirImageView.setImageResource(R.drawable.ic_quran_page_right);
                break;
            case PAGE_DIR_LEFT:
                pageDirImageView.setImageResource(R.drawable.ic_quran_page_left);
                break;
            default:
                throw new IllegalArgumentException("Invalid page dir.");
        }

    }

}
