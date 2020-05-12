package app.quranhub.downloads_manager.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import app.quranhub.R;
import app.quranhub.utils.DialogUtil;

/**
 * A {@code DialogFragment} to confirm deletion action.
 * Use the {@link DeleteConfirmationDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeleteConfirmationDialogFragment extends DialogFragment {

    private static final String TAG = DeleteConfirmationDialogFragment.class.getSimpleName();

    private static final String ARG_DIALOG_TITLE = "ARG_DIALOG_TITLE";
    private static final String ARG_DIALOG_DESCRIPTION = "ARG_DIALOG_DESCRIPTION";
    private static final String ARG_DELETE_POSITION = "ARG_DELETE_POSITION";

    private String title;
    private String description;
    private int deletePosition;

    private Unbinder butterknifeUnbinder;

    @BindView(R.id.tv_title)
    TextView titleTextView;
    @BindView(R.id.tv_description)
    TextView descriptionTextView;

    private DeleteConfirmationCallbacks callbacks;

    public DeleteConfirmationDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title       Dialog title.
     * @param description Dialog description.
     * @return A new instance of fragment DeleteConfirmationDialogFragment.
     */
    public static DeleteConfirmationDialogFragment newInstance(String title, String description
            , int deletePosition) {
        DeleteConfirmationDialogFragment fragment = new DeleteConfirmationDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DIALOG_TITLE, title);
        args.putString(ARG_DIALOG_DESCRIPTION, description);
        args.putInt(ARG_DELETE_POSITION, deletePosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DeleteConfirmationCallbacks) {
            callbacks = (DeleteConfirmationCallbacks) context;
        } else if (getParentFragment() instanceof DeleteConfirmationCallbacks) {
            callbacks = (DeleteConfirmationCallbacks) getParentFragment();
        } else {
            throw new RuntimeException("The containing fragment or activity must implement" +
                    " DeleteConfirmationDialogFragment#DeleteConfirmationCallbacks interface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_DIALOG_TITLE);
            description = getArguments().getString(ARG_DIALOG_DESCRIPTION);
            deletePosition = getArguments().getInt(ARG_DELETE_POSITION);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View dialogView = inflater.inflate(R.layout.dialog_confirmation, container, false);
        butterknifeUnbinder = ButterKnife.bind(this, dialogView);
        initDialogView();
        return dialogView;
    }

    private void initDialogView() {
        titleTextView.setText(title);
        descriptionTextView.setText(description);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        DialogUtil.wrapDialogHeight(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        butterknifeUnbinder.unbind();
    }

    @OnClick(R.id.btn_cancel)
    void onCancelBtnClick() {
        dismiss();
    }

    @OnClick(R.id.btn_confirm)
    void onConfirmBtnClick() {
        callbacks.onConfirmDelete(deletePosition);
        dismiss();
    }

    public interface DeleteConfirmationCallbacks {
        void onConfirmDelete(int deletePosition);
    }

}
