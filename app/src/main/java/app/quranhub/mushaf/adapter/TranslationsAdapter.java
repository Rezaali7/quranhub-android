package app.quranhub.mushaf.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.quranhub.R;
import app.quranhub.mushaf.data.entity.TranslationBook;
import app.quranhub.mushaf.model.DisplayableTranslation;
import app.quranhub.mushaf.utils.NetworkUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TranslationsAdapter extends RecyclerView.Adapter<TranslationsAdapter.ViewHolder>
        implements Filterable {

    private static final String TAG = TranslationsAdapter.class.getSimpleName();

    @Nullable
    private List<DisplayableTranslation> originalTranslations;
    @Nullable
    private List<DisplayableTranslation> filteredTranslations;
    private String searchText = "";
    @Nullable
    private String selectedBookId;
    private ItemClickListener listener;


    public TranslationsAdapter(@Nullable List<DisplayableTranslation> translations, @Nullable String selectedBookId
            , ItemClickListener listener) {
        sortTranslationList(translations);
        this.originalTranslations = translations;
        this.filteredTranslations = translations;
        this.selectedBookId = selectedBookId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_translation, parent, false);

        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DisplayableTranslation t = filteredTranslations.get(position);

        holder.bookNameTextView.setText(t.getName());
        holder.authorNameTextView.setText(t.getAuthor());

        if (t.getDownloadStatus() == NetworkUtil.STATUS_DOWNLOADED && t.getId().equals(selectedBookId)) {
            holder.selectedImageView.setVisibility(View.VISIBLE);
        } else {
            holder.selectedImageView.setVisibility(View.INVISIBLE);
        }

        if (t.getDownloadStatus() == NetworkUtil.STATUS_DOWNLOADED) {
            holder.actionImageButton.setVisibility(View.INVISIBLE);
            holder.downloadProgressBar.setVisibility(View.INVISIBLE);
            holder.downloadLevelProgressBar.setVisibility(View.INVISIBLE);

        } else if (t.getDownloadStatus() == NetworkUtil.STATUS_DOWNLOADING) {
            holder.actionImageButton.setVisibility(View.VISIBLE);
            holder.actionImageButton.setImageResource(R.drawable.ic_close);
            holder.downloadProgressBar.setVisibility(View.VISIBLE);
            holder.downloadLevelProgressBar.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.downloadLevelProgressBar.setProgress(t.getDownloadLevelPercentage(), true);
            } else {
                holder.downloadLevelProgressBar.setProgress(t.getDownloadLevelPercentage());
            }
        } else {
            // not downloaded
            holder.actionImageButton.setVisibility(View.VISIBLE);
            holder.actionImageButton.setImageResource(R.drawable.download_action_green_ic);
            holder.downloadProgressBar.setVisibility(View.INVISIBLE);
            holder.downloadLevelProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return filteredTranslations != null ? filteredTranslations.size() : 0;
    }

    public void setTranslations(@Nullable List<DisplayableTranslation> translations) {
        sortTranslationList(translations);
        this.originalTranslations = translations;
        getFilter().filter(searchText);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<DisplayableTranslation> resultTranslations = new ArrayList<>();
                if (constraint.length() == 0) {
                    resultTranslations = originalTranslations;
                } else {
                    for (DisplayableTranslation t : originalTranslations) {
                        if (t.getName().toLowerCase().contains(constraint.toString().toLowerCase())
                                || t.getAuthor().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            resultTranslations.add(t);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = resultTranslations;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                searchText = constraint.toString();
                filteredTranslations = (List<DisplayableTranslation>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_book_name)
        TextView bookNameTextView;
        @BindView(R.id.tv_author_name)
        TextView authorNameTextView;
        @BindView(R.id.btn_action)
        ImageButton actionImageButton;
        @BindView(R.id.progress_download)
        ProgressBar downloadProgressBar;
        @BindView(R.id.progress_download_level)
        ProgressBar downloadLevelProgressBar;
        @BindView(R.id.iv_selected)
        ImageView selectedImageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            DisplayableTranslation t = filteredTranslations.get(position);
            if (t.getDownloadStatus() == NetworkUtil.STATUS_DOWNLOADED) {
                listener.onTranslationClick(t.getTranslationBook(), getAdapterPosition());
            }
        }

        @OnClick(R.id.btn_action)
        void onActionButtonClicked() {
            int position = getAdapterPosition();
            DisplayableTranslation t = filteredTranslations.get(position);
            if (t.getDownloadStatus() == NetworkUtil.STATUS_NOT_DOWNLOADED) {
                listener.onDownloadTranslationClick(t.getTranslationBook(), position);
            } else if (t.getDownloadStatus() == NetworkUtil.STATUS_DOWNLOADING) {
                listener.onCancelDownloadTranslationClick(t.getTranslationBook(), position);
            }
        }
    }

    private void sortTranslationList(List<DisplayableTranslation> displayableTranslations) {
        Collections.sort(displayableTranslations, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
    }


    /**
     * Used in handling items clicks
     */
    public interface ItemClickListener {
        void onTranslationClick(TranslationBook translationBook, int clickedItemIndex);

        void onDownloadTranslationClick(TranslationBook translationBook, int clickedItemIndex);

        void onCancelDownloadTranslationClick(TranslationBook translationBook, int clickedItemIndex);
    }

}
