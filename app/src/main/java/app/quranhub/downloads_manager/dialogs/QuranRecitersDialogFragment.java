package app.quranhub.downloads_manager.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.quranhub.mushaf.data.entity.Sheikh;
import app.quranhub.mushaf.network.ApiClient;
import app.quranhub.settings.dialogs.OptionsListAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import app.quranhub.Constants;
import app.quranhub.R;
import app.quranhub.downloads_manager.network.api.RecitersApi;
import app.quranhub.downloads_manager.network.model.RecitersResponse;
import app.quranhub.mushaf.data.dao.SheikhDao;
import app.quranhub.mushaf.data.db.UserDatabase;
import app.quranhub.utils.DialogUtil;
import app.quranhub.utils.FragmentUtil;
import app.quranhub.utils.PreferencesUtils;

/**
 * A {@code DialogFragment} that displays the available Quran reciters for the user to choose from.
 * <p>
 * Activities or parent fragments that shows this DialogFragment must implement the
 * {@link ReciterSelectionListener} interface to handle interaction events.
 * Use the {@link QuranRecitersDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuranRecitersDialogFragment extends DialogFragment
        implements OptionsListAdapter.ItemClickListener {
    
    private static final String TAG = QuranRecitersDialogFragment.class.getSimpleName();
    
    private static final String ARG_RECITATION_ID = "ARG_RECITATION_ID";
    private static final String ARG_SELECTED_RECITER_ID = "ARG_SELECTED_RECITER_ID";

    private int recitationId;
    @Nullable
    private String selectedReciterId;

    private int selectedReciterIndex = -1;

    @BindView(R.id.tv_msg_downloaded_reciters_only)
    TextView downloadedRecitersOnlyMsgTextView;
    @BindView(R.id.rv_reciters)
    RecyclerView recitersRecyclerView;
    @BindView(R.id.tv_msg_internet_connection_failed)
    TextView internetConnectionFailedMsgTextView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_select)
    Button selectButton;

    private Unbinder butterknifeUnbinder;

    private OptionsListAdapter adapter;

    private ReciterSelectionListener reciterSelectionListener;

    private Call<RecitersResponse> recitersWebCall;
    private List<Sheikh> reciters;

    public QuranRecitersDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recitationId A recitation ID as in {@link Constants.RECITATIONS}.
     * @return A new instance of fragment QuranRecitersDialogFragment.
     */
    public static QuranRecitersDialogFragment newInstance(int recitationId) {
        return newInstance(recitationId, null);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recitationId A recitation ID as in {@link Constants.RECITATIONS}.
     * @param selectedReciterId The current selected reciter ID.
     * @return A new instance of fragment QuranRecitersDialogFragment.
     */
    public static QuranRecitersDialogFragment newInstance(int recitationId,
                                                          @Nullable String selectedReciterId) {
        QuranRecitersDialogFragment recitersDialogFragment = new QuranRecitersDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_RECITATION_ID, recitationId);
        args.putString(ARG_SELECTED_RECITER_ID, selectedReciterId);
        recitersDialogFragment.setArguments(args);
        return recitersDialogFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof  ReciterSelectionListener) {
            reciterSelectionListener = (ReciterSelectionListener) context;
        }
        else if (getParentFragment() instanceof ReciterSelectionListener) {
            reciterSelectionListener = (ReciterSelectionListener) getParentFragment();
        } else {
            throw new RuntimeException("Activities or parent fragments that shows this DialogFragment"
                    + " must implement ReciterSelectionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            recitationId = getArguments().getInt(ARG_RECITATION_ID);
            selectedReciterId = getArguments().getString(ARG_SELECTED_RECITER_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_quran_reciters, container);
        butterknifeUnbinder = ButterKnife.bind(this, dialogView);
        initDialogView();
        return dialogView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

    private void initDialogView() {
        downloadedRecitersOnlyMsgTextView.setVisibility(View.GONE);
        internetConnectionFailedMsgTextView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        selectButton.setEnabled(false);
    }

    private void setupRecitersRecyclerView() {
        if (reciters != null) {
            List<String> recitersNames = new ArrayList<>();
            for (int i = 0; i < reciters.size(); i++) {
                Sheikh r = reciters.get(i);
                recitersNames.add(r.getLocalizedName(requireContext()));
                if (r.getId().equals(selectedReciterId)) {
                    selectedReciterIndex = i;
                    selectButton.setEnabled(true);
                }
            }

            recitersRecyclerView.setHasFixedSize(true);
            recitersRecyclerView.setLayoutManager(new LinearLayoutManager(
                    getContext(), RecyclerView.VERTICAL, false));
            recitersRecyclerView.addItemDecoration(new DividerItemDecoration(
                    getContext(), DividerItemDecoration.VERTICAL));
            adapter = new OptionsListAdapter(recitersNames, selectedReciterIndex, this);
            recitersRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        DialogUtil.adjustDialogSize(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecitersApi recitersApi = ApiClient.getClient().create(RecitersApi.class);
        recitersWebCall = recitersApi.getQuranReciters(recitationId);
        recitersWebCall.enqueue(new Callback<RecitersResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecitersResponse> call,
                                   @NonNull Response<RecitersResponse> response) {

                if (FragmentUtil.isSafeFragment(QuranRecitersDialogFragment.this)) {
                    RecitersResponse recitersResponse = response.body();
                    if (recitersResponse != null) {
                        reciters = recitersResponse.getReciters();
                        setupRecitersRecyclerView();
                    } else {
                        Log.e(TAG, "RecitersApi#getQuranReciters service response body is null");
                        Toast.makeText(requireContext(), getString(R.string.error_general), Toast.LENGTH_SHORT).show();
                    }

                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecitersResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "RecitersApi#getQuranReciters service call failed");
                if (FragmentUtil.isSafeFragment(QuranRecitersDialogFragment.this)) {

                    // try to display the downloaded reciters
                    loadRecitersFromDb();

                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void loadRecitersFromDb() {
        // TODO load from DB only for a selected aya, or page or sura, to guarantee reciter is downloaded
        new AsyncTask<Void, Void, List<Sheikh>>() {

            @Override
            protected List<Sheikh> doInBackground(Void... voids) {
                if (getContext() != null) {
                    SheikhDao sheikhDao = UserDatabase.getInstance(getContext()).getSheikhDao();
                    return sheikhDao.getAllForRecitation(recitationId);
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Sheikh> recitersList) {
                if (recitersList != null &&
                        FragmentUtil.isSafeFragment(QuranRecitersDialogFragment.this)) {
                    reciters = recitersList;
                    if (reciters.size() > 0) {
                        downloadedRecitersOnlyMsgTextView.setVisibility(View.VISIBLE);
                        setupRecitersRecyclerView();
                    } else {
                        // User hasn't downloaded any Quran audio before
                        internetConnectionFailedMsgTextView.setVisibility(View.VISIBLE);
                    }

                    progressBar.setVisibility(View.GONE);
                }
            }
        }.execute();
    }

    @Override
    public void onItemClick(int clickedItemIndex) {
        selectedReciterIndex = clickedItemIndex;
        if (!selectButton.isEnabled()) selectButton.setEnabled(true);
    }

    @OnClick(R.id.btn_back)
    void onBackClick() {
        dismiss();
    }

    @SuppressLint("StaticFieldLeak")
    @OnClick(R.id.btn_select)
    void onSelectClick() {
        Sheikh selectedReciter = reciters.get(selectedReciterIndex);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                selectButton.setEnabled(false);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                if (FragmentUtil.isSafeFragment(QuranRecitersDialogFragment.this)) {

                    // Store selected reciter in DB
                    UserDatabase userDatabase = UserDatabase.getInstance(requireContext());
                    if (userDatabase.getSheikhDao().getById(selectedReciter.getId()) == null) {
                        userDatabase.getSheikhDao()
                                .insert(selectedReciter);
                    }

                    // persist selected reciter as preference if recitation id matches the one in preferences
                    int recitationIdPreference = PreferencesUtils.getRecitationSetting(requireContext());
                    if (recitationIdPreference == recitationId) {
                        PreferencesUtils.persistReciterSheikhSetting(requireContext(), selectedReciter.getId());
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                reciterSelectionListener.onReciterSelected(recitationId, selectedReciter);
                dismiss();
            }
        }.execute();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (recitersWebCall != null) recitersWebCall.cancel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        butterknifeUnbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        reciterSelectionListener = null;
    }

    /**
     * This interface must be implemented by activities or parent fragments that contain this
     * dialog fragment to allow an interaction in this fragment to be communicated
     * to the activity or parent fragment.
     */
    public interface ReciterSelectionListener {
        void onReciterSelected(int recitationId, @NonNull Sheikh reciter);
    }
}
