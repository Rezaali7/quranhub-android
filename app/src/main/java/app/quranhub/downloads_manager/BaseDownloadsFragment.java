package app.quranhub.downloads_manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import app.quranhub.R;
import app.quranhub.downloads_manager.adapters.DownloadsAdapter;
import app.quranhub.downloads_manager.model.DisplayableDownload;
import app.quranhub.downloads_manager.network.QuranAudioDownloaderService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Base for downloads screen fragments.
 * <p>
 * Args [optional]:  (ARG_DESCRIPTION -> String), (ARG_EDITABLE -> boolean)
 * </p>
 * <p>
 * Activities or parent fragments containing a subclass of {@link BaseDownloadsFragment}
 * must implement {@link DownloadsManagerNavigationCallbacks} interface.
 * </p>
 */
public abstract class BaseDownloadsFragment extends Fragment
        implements Editable, DownloadsAdapter.ItemClickListener {

    private static final String TAG = BaseDownloadsFragment.class.getSimpleName();

    protected static final String ARG_DESCRIPTION = "ARG_DESCRIPTION";
    protected static final String ARG_EDITABLE = "ARG_EDITABLE";

    private static final String STATE_EDITABLE = "STATE_EDITABLE";

    @BindView(R.id.tv_description)
    TextView descriptionTextView;
    @BindView(R.id.rv_downloads)
    RecyclerView downloadsRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private Unbinder butterknifeUnbinder;

    private List<DisplayableDownload> displayableDownloads;
    private DownloadsAdapter downloadsAdapter;

    @Nullable
    private String description;
    private boolean editable = false;

    protected DownloadsManagerNavigationCallbacks navigationCallbacks;

    public BaseDownloadsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof DownloadsManagerNavigationCallbacks) {
            navigationCallbacks = (DownloadsManagerNavigationCallbacks) context;
        } else if (getParentFragment() instanceof DownloadsManagerNavigationCallbacks) {
            navigationCallbacks = (DownloadsManagerNavigationCallbacks) getParentFragment();
        } else {
            throw new RuntimeException("Activities or parent fragments containing a subclass of " +
                    "BaseDownloadsFragment must implement DownloadsManagerNavigationCallbacks interface.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            description = getArguments().getString(ARG_DESCRIPTION);
            editable = getArguments().getBoolean(ARG_EDITABLE, false);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_downloads, container, false);
        butterknifeUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            editable = savedInstanceState.getBoolean(STATE_EDITABLE);
        }

        setupDescription();
        setupDownloadsRecyclerView();
    }

    private void setupDescription() {
        if (description != null) {
            descriptionTextView.setText(description);
        } else {
            descriptionTextView.setVisibility(View.GONE);
        }
    }

    private void setupDownloadsRecyclerView() {
        downloadsRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        downloadsRecyclerView.setLayoutManager(layoutManager);

        // add dividers between RecyclerView items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(),
                layoutManager.getOrientation());
        downloadsRecyclerView.addItemDecoration(dividerItemDecoration);

        displayableDownloads = new ArrayList<>();
        downloadsAdapter = new DownloadsAdapter(displayableDownloads, this, editable);
        downloadsRecyclerView.setAdapter(downloadsAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        displayDownloadItems();
    }

    @SuppressLint("StaticFieldLeak")
    private void displayDownloadItems() {
        new AsyncTask<Void, Void, List<DisplayableDownload>>() {

            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected List<DisplayableDownload> doInBackground(Void... voids) {
                return provideDisplayableDownloads();
            }

            @Override
            protected void onPostExecute(List<DisplayableDownload> downloads) {
                Log.d(TAG, "Provided displayableDownloads=" + downloads);
                displayableDownloads = downloads;
                downloadsAdapter.setDisplayableDownloads(displayableDownloads);
                progressBar.setVisibility(View.GONE);
            }
        }.execute();
    }

    protected void refresh() {
        displayDownloadItems();
    }

    public boolean getEditable() {
        return editable;
    }

    /**
     * This method will be called from a background thread. You don't have to create a new one.
     */
    @NonNull
    protected abstract List<DisplayableDownload> provideDisplayableDownloads();

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_EDITABLE, editable);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownloadFinishEvent(QuranAudioDownloaderService.DownloadFinishEvent event) {
        refresh();
    }

    ;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        butterknifeUnbinder.unbind();
    }

    @Override
    public void setEditable(boolean isEditable) {
        editable = isEditable;
        if (downloadsAdapter != null) {
            downloadsAdapter.setEdit(editable);
        }
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    protected DownloadsAdapter getDownloadsAdapter() {
        return downloadsAdapter;
    }

    protected List<DisplayableDownload> getDisplayableDownloads() {
        return displayableDownloads;
    }

    /**
     * Activities or parent fragments containing a subclass of {@link BaseDownloadsFragment}
     * must implement this interface.
     */
    public interface DownloadsManagerNavigationCallbacks {

        void gotoDownloadsRecitations();

        void gotoDownloadsReciters(int recitationId);

        void gotoDownloadsSuras(int recitationId, @NonNull String reciterId, @NonNull String reciterName);

        void openRecitersDialog(int recitationId);

        void openAudioDownloadAmountDialog(int recitationId, @NonNull String reciterId);

    }
}
