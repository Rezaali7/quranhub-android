package app.quranhub.downloads_manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.quranhub.R;
import app.quranhub.downloads_manager.dialogs.DeleteConfirmationDialogFragment;
import app.quranhub.downloads_manager.model.DisplayableDownload;
import app.quranhub.downloads_manager.network.api.RecitersApi;
import app.quranhub.downloads_manager.network.model.RecitersResponse;
import app.quranhub.downloads_manager.utils.QuranAudioDeleteUtils;
import app.quranhub.mushaf.data.db.UserDatabase;
import app.quranhub.mushaf.data.entity.Sheikh;
import app.quranhub.mushaf.data.entity.SheikhRecitation;
import app.quranhub.mushaf.network.ApiClient;
import retrofit2.Call;
import retrofit2.Response;

public class DownloadsRecitersFragment extends BaseDownloadsFragment
        implements DeleteConfirmationDialogFragment.DeleteConfirmationCallbacks {

    private static final String TAG = DownloadsRecitersFragment.class.getSimpleName();

    private static final String ARG_RECITATION_ID = "ARG_RECITATION_ID";

    private int recitationId;

    private List<Sheikh> reciters;

    public static DownloadsRecitersFragment newInstance(@NonNull Context context, int recitationId) {
        return newInstance(context, recitationId, false);
    }

    public static DownloadsRecitersFragment newInstance(@NonNull Context context, int recitationId
            , boolean isEditable) {
        DownloadsRecitersFragment recitersFragment = new DownloadsRecitersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_RECITATION_ID, recitationId);
        args.putString(ARG_DESCRIPTION, context.getString(R.string.description_manage_reciters_downloads));
        args.putBoolean(ARG_EDITABLE, isEditable);
        recitersFragment.setArguments(args);
        return recitersFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            recitationId = getArguments().getInt(ARG_RECITATION_ID);
        }
    }

    @NonNull
    @Override
    protected List<DisplayableDownload> provideDisplayableDownloads() {

        List<DisplayableDownload> displayableDownloadsList = new ArrayList<>();

        RecitersApi recitersApi = ApiClient.getClient().create(RecitersApi.class);
        Call<RecitersResponse> recitersCall = recitersApi.getQuranReciters(recitationId);
        try {
            Response<RecitersResponse> response = recitersCall.execute();
            RecitersResponse recitersResponse = response.body();
            if (recitersResponse != null) {
                reciters = recitersResponse.getReciters();
            } else {
                Log.e(TAG, "recitersResponse is null!");
                reciters = retrieveLocalReciters();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to retrieve reciters from server.");
            reciters = retrieveLocalReciters();
        }

        // process reciters list
        for (Sheikh r : reciters) {
            UserDatabase userDatabase = UserDatabase.getInstance(requireContext());

            DisplayableDownload displayableDownload = new DisplayableDownload(
                    r.getLocalizedName(requireContext()));

            List<Integer> downloadedSurasIds = userDatabase.getSheikhRecitationDao()
                    .getSurasIdsForReciterInRecitation(recitationId, r.getId());
            displayableDownload.setDownloadedAmount(
                    getString(R.string.downloaded_amount_suras, downloadedSurasIds.size()));

            displayableDownload.setDownloadable(downloadedSurasIds.size() < 114);
            displayableDownload.setDeletable(downloadedSurasIds.size() > 0);

            displayableDownloadsList.add(displayableDownload);
        }

        return displayableDownloadsList;
    }

    private List<Sheikh> retrieveLocalReciters() {
        return UserDatabase.getInstance(requireContext())
                .getSheikhDao().getAllForRecitation(recitationId);
    }

    @Override
    public void onClickItem(DisplayableDownload displayableDownload, int position) {
        Sheikh reciter = reciters.get(position);
        navigationCallbacks.gotoDownloadsSuras(recitationId, reciter.getId(), reciter.getArName());
    }

    @Override
    public void onDeleteItem(DisplayableDownload displayableDownload, int position) {
        DeleteConfirmationDialogFragment confirmationDialog = DeleteConfirmationDialogFragment.newInstance(
                getString(R.string.confirm_delete_title),
                getString(R.string.confirm_delete_description_reciters), position);
        confirmationDialog.show(getChildFragmentManager(), "DeleteConfirmationDialogFragment");
    }

    @Override
    public void onConfirmDelete(int deletePosition) {
        QuranAudioDeleteUtils.deleteReciterAudio(requireContext(), recitationId
                , reciters.get(deletePosition).getId(), this::refresh);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onDownloadItem(DisplayableDownload displayableDownload, int position) {
        Sheikh reciter = reciters.get(position);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                UserDatabase userDatabase = UserDatabase.getInstance(requireContext());
                if (userDatabase.getSheikhDao().getById(reciter.getId()) == null) {
                    userDatabase.getSheikhDao().insert(reciter);
                }
                if (userDatabase.getSheikhRecitationDao().get(recitationId, reciter.getId()) == null) {
                    userDatabase.getSheikhRecitationDao().insert(
                            new SheikhRecitation(recitationId, reciter.getId()));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                navigationCallbacks.openAudioDownloadAmountDialog(recitationId, reciter.getId());
            }
        }.execute();
    }
}
