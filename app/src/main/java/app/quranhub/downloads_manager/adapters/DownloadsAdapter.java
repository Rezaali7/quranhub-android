package app.quranhub.downloads_manager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.quranhub.R;
import app.quranhub.downloads_manager.model.DisplayableDownload;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.ViewHolder> {

    private static final String TAG = DownloadsAdapter.class.getSimpleName();

    @NonNull
    private List<DisplayableDownload> displayableDownloads;
    @NonNull
    private ItemClickListener clickListener;
    private boolean edit = false;

    public DownloadsAdapter(@NonNull List<DisplayableDownload> displayableDownloads,
                            @NonNull ItemClickListener clickListener) {
        this.displayableDownloads = displayableDownloads;
        this.clickListener = clickListener;
    }

    public DownloadsAdapter(@NonNull List<DisplayableDownload> displayableDownloads,
                            @NonNull ItemClickListener clickListener, boolean edit) {
        this.displayableDownloads = displayableDownloads;
        this.clickListener = clickListener;
        this.edit = edit;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_download, parent, false);

        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(displayableDownloads.get(position));
    }

    @Override
    public int getItemCount() {
        return displayableDownloads.size();
    }

    @NonNull
    public List<DisplayableDownload> getDisplayableDownloads() {
        return displayableDownloads;
    }

    public void setDisplayableDownloads(@NonNull List<DisplayableDownload> displayableDownloads) {
        this.displayableDownloads = displayableDownloads;
        notifyDataSetChanged();
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView nameTextView;
        @BindView(R.id.tv_downloaded_amount)
        TextView downloadedAmountTextView;
        @BindView(R.id.ib_action)
        ImageButton actionImageButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(@NonNull DisplayableDownload displayableDownload) {
            nameTextView.setText(displayableDownload.getName());
            if (displayableDownload.getDownloadedAmount() != null) {
                downloadedAmountTextView.setText(displayableDownload.getDownloadedAmount());
            }
            if (edit) {  // edit mode
                if (displayableDownload.isDeletable()) {
                    actionImageButton.setImageResource(R.drawable.ic_delete);
                    actionImageButton.setVisibility(View.VISIBLE);
                } else {
                    actionImageButton.setVisibility(View.INVISIBLE);
                }
            } else {  // download mode
                if (displayableDownload.isDownloadable()) {
                    actionImageButton.setImageResource(R.drawable.download_action_green_ic);
                    actionImageButton.setVisibility(View.VISIBLE);
                } else {
                    actionImageButton.setVisibility(View.INVISIBLE);
                }
            }
        }

        @OnClick(R.id.ll_content)
        void onContentClick() {
            DisplayableDownload clickedDisplayableDownload = displayableDownloads.get(getAdapterPosition());
            clickListener.onClickItem(clickedDisplayableDownload, getAdapterPosition());
        }

        @OnClick(R.id.ib_action)
        void onActionButtonClick() {
            DisplayableDownload clickedDisplayableDownload = displayableDownloads.get(getAdapterPosition());
            if (edit) {
                clickListener.onDeleteItem(clickedDisplayableDownload, getAdapterPosition());
            } else {
                clickListener.onDownloadItem(clickedDisplayableDownload, getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener {

        void onClickItem(DisplayableDownload displayableDownload, int position);

        void onDeleteItem(DisplayableDownload displayableDownload, int position);

        void onDownloadItem(DisplayableDownload displayableDownload, int position);

    }
}
