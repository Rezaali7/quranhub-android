package app.quranhub.first_wizard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import app.quranhub.R;
import app.quranhub.utils.interfaces.Searchable;

/**
 * Activities that contain this fragment must implement the
 * {@link OnOptionClickListener} interface
 * to handle interaction events.
 * Use the {@link OptionsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OptionsListFragment extends Fragment implements OptionsListAdapter.ItemClickListener, Searchable {

    private static final String TAG = OptionsListFragment.class.getSimpleName();

    private static final String STATE_SEARCH_TEXT = "STATE_SEARCH_TEXT";

    private static final String ARG_OPTIONS = "ARG_OPTIONS";
    private static final String ARG_OPTIONS_THUMBNAILS = "ARG_OPTIONS_THUMBNAILS";
    private static final String ARG_SELECTED_OPTION_POSITION = "ARG_SELECTED_OPTION_POSITION";
    private static final String ARG_REQUEST_CODE = "ARG_REQUEST_CODE";

    private int requestCode;
    @NonNull
    private List<String> options;
    @Nullable
    private int[] optionsThumbnailsDrawableIds;
    private int selectedOptionPosition;

    private OnOptionClickListener listener;

    @BindView(R.id.rv_options)
    RecyclerView optionsRecyclerView;

    OptionsListAdapter optionsListAdapter;

    private String searchText = "";

    private Unbinder butterknifeUnbiner;

    public OptionsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static OptionsListFragment newInstance(@NonNull ArrayList<String> options
            , @Nullable int [] optionsThumbnailsDrawableIds, int selectedOptionPosition, int requestCode) {
        OptionsListFragment fragment = new OptionsListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_OPTIONS, options);
        args.putIntArray(ARG_OPTIONS_THUMBNAILS, optionsThumbnailsDrawableIds);
        args.putInt(ARG_SELECTED_OPTION_POSITION, selectedOptionPosition);
        args.putInt(ARG_REQUEST_CODE, requestCode);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static OptionsListFragment newInstance(@NonNull Context context, @NonNull int[] optionsStrResIds
            , int selectedOptionPosition, int requestCode) {
        ArrayList<String> options = new ArrayList<>();
        for (int strResId : optionsStrResIds) {
            options.add(context.getString(strResId));
        }
        return newInstance(options, null, selectedOptionPosition, requestCode);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static OptionsListFragment newInstance(@NonNull Context context, @NonNull int[] optionsStrResIds
            , @Nullable int [] optionsThumbnailsDrawableIds, int selectedOptionPosition, int requestCode) {
        ArrayList<String> options = new ArrayList<>();
        for (int strResId : optionsStrResIds) {
            options.add(context.getString(strResId));
        }
        return newInstance(options, optionsThumbnailsDrawableIds, selectedOptionPosition, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            options = getArguments().getStringArrayList(ARG_OPTIONS);
            optionsThumbnailsDrawableIds = getArguments().getIntArray(ARG_OPTIONS_THUMBNAILS);
            selectedOptionPosition = getArguments().getInt(ARG_SELECTED_OPTION_POSITION);
            requestCode = getArguments().getInt(ARG_REQUEST_CODE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_options_list, container, false);
        butterknifeUnbiner = ButterKnife.bind(this, view);
        initOptionsRecyclerView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            String search = savedInstanceState.getString(STATE_SEARCH_TEXT, null);
            if (search != null) {
                searchText = search;
                search(searchText);
            }
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (searchText.length() != 0) {
            outState.putString(STATE_SEARCH_TEXT, searchText);
        }
    }

    private void initOptionsRecyclerView() {
        optionsRecyclerView.setHasFixedSize(true);
        optionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()
                , RecyclerView.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        optionsRecyclerView.addItemDecoration(dividerItemDecoration);
        optionsListAdapter = new OptionsListAdapter(options, optionsThumbnailsDrawableIds
                , selectedOptionPosition, this);
        optionsRecyclerView.setAdapter(optionsListAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOptionClickListener) {
            listener = (OnOptionClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnOptionClickListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        butterknifeUnbiner.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onItemClick(int clickedItemIndex) {
        listener.onOptionClicked(requestCode, options.get(clickedItemIndex), clickedItemIndex);
    }

    @Override
    public void search(String text) {
        searchText = text;
        optionsListAdapter.getFilter().filter(text);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnOptionClickListener {
        void onOptionClicked(int requestCode, @NonNull String option, int position);
    }
}
