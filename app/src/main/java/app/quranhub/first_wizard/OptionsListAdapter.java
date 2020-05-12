package app.quranhub.first_wizard;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import app.quranhub.R;

public class OptionsListAdapter extends RecyclerView.Adapter<OptionsListAdapter.ViewHolder> implements Filterable {

    private static final String TAG = OptionsListAdapter.class.getSimpleName();

    @NonNull
    private List<String> optionsList;
    @NonNull
    private List<String> filteredOptionsList;
    @Nullable
    private int[] optionsThumbnailsDrawableIds;
    @Nullable
    private int[] filteredOptionsThumbnailsDrawableIds;
    private int selectedOptionIndex;
    @NonNull
    private ItemClickListener itemClickListener;

    public OptionsListAdapter(@NonNull List<String> optionsList, @NonNull ItemClickListener listener) {
        this.optionsList = optionsList;
        this.filteredOptionsList = optionsList;
        this.itemClickListener = listener;
        selectedOptionIndex = -1;
    }

    public OptionsListAdapter(@NonNull List<String> optionsList, int selectedOptionIndex
            , @NonNull ItemClickListener listener) {
        this.optionsList = optionsList;
        this.filteredOptionsList = optionsList;
        this.selectedOptionIndex = selectedOptionIndex;
        this.itemClickListener = listener;
    }

    public OptionsListAdapter(@NonNull List<String> optionsList, @Nullable int [] optionsThumbnailsDrawableIds
            , int selectedOptionIndex, @NonNull ItemClickListener listener) {
        this.optionsList = optionsList;
        this.filteredOptionsList = optionsList;
        this.optionsThumbnailsDrawableIds = optionsThumbnailsDrawableIds;
        this.filteredOptionsThumbnailsDrawableIds = optionsThumbnailsDrawableIds;
        this.selectedOptionIndex = selectedOptionIndex;
        this.itemClickListener = listener;
    }

    @NonNull
    public List<String> getOptionsList() {
        return optionsList;
    }

    public void setOptionsList(@NonNull List<String> optionsList) {
        this.optionsList = optionsList;
        this.filteredOptionsList = optionsList;
        notifyDataSetChanged();
    }

    public void setOptions(@NonNull List<String> optionsList
            , @Nullable int [] optionsThumbnailsDrawableIds) {
        this.optionsList = optionsList;
        this.filteredOptionsList = optionsList;
        this.optionsThumbnailsDrawableIds = optionsThumbnailsDrawableIds;
        this.filteredOptionsThumbnailsDrawableIds = optionsThumbnailsDrawableIds;
        notifyDataSetChanged();
    }

    public int getSelectedOptionIndex() {
        return selectedOptionIndex;
    }

    public void setSelectedOptionIndex(int selectedOptionIndex) {
        this.selectedOptionIndex = selectedOptionIndex;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_option, parent, false);

        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (filteredOptionsThumbnailsDrawableIds != null) {
            int drawableResId = filteredOptionsThumbnailsDrawableIds[position];
            holder.optionThumbnailImageView.setVisibility(View.VISIBLE);
            holder.optionThumbnailImageView.setImageResource(drawableResId);
        } else {
            holder.optionThumbnailImageView.setVisibility(View.GONE);
        }
        String option = filteredOptionsList.get(position);
        holder.optionNameTextView.setText(option);
        if (position == selectedOptionIndex) {
            holder.checkBoxImageView.setVisibility(View.VISIBLE);
        } else {
            holder.checkBoxImageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (filteredOptionsList == null)
            return 0;
        return filteredOptionsList.size();
    }

    @Override
    public Filter getFilter() {
        // TODO refactor & enhance Filter
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<String> filterOptionsResult = new ArrayList<>();
                List<Integer> filterThumbnailsResult = null;
                if (constraint.length() == 0) {
                    filterOptionsResult = optionsList;
                    if (optionsThumbnailsDrawableIds != null) {
                        filterThumbnailsResult = new ArrayList<>();
                        for (int d: optionsThumbnailsDrawableIds) {
                            filterThumbnailsResult.add(d);
                        }
                    }
                } else {
                    if (optionsThumbnailsDrawableIds != null) {
                        filterThumbnailsResult = new ArrayList<>();
                    }
                    for (int i = 0; i<optionsList.size(); i++) {
                        String opt = optionsList.get(i);
                        if (opt.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filterOptionsResult.add(opt);
                            if (filterThumbnailsResult != null) {
                                filterThumbnailsResult.add(optionsThumbnailsDrawableIds[i]);
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = new Pair<>(filterOptionsResult, filterThumbnailsResult);
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                Pair<List<String>, List<Integer>> filterResult =
                        ((Pair<List<String>, List<Integer>>) results.values);
                filteredOptionsList = filterResult.first;
                if (filterResult.second != null) {
                    filteredOptionsThumbnailsDrawableIds = new int[filterResult.second.size()];
                    for (int i = 0; i < filterResult.second.size(); i++) {
                        filteredOptionsThumbnailsDrawableIds[i] = filterResult.second.get(i);
                    }
                }
                else {
                    filteredOptionsThumbnailsDrawableIds = null;
                }
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_option_thumbnail)
        ImageView optionThumbnailImageView;
        @BindView(R.id.tv_option_name)
        TextView optionNameTextView;
        @BindView(R.id.iv_check_box)
        ImageView checkBoxImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            setSelectedOptionIndex(getAdapterPosition());
            itemClickListener.onItemClick(selectedOptionIndex);
        }
    }

    /**
     * Used in handling items clicks
     */
    public interface ItemClickListener {
        void onItemClick(int clickedItemIndex);
    }

}
