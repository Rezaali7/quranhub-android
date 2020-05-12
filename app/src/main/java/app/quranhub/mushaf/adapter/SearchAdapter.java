package app.quranhub.mushaf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.quranhub.mushaf.model.SearchModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import app.quranhub.R;
import app.quranhub.mushaf.listener.ItemSelectionListener;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context context;
    private ItemSelectionListener<SearchModel> listener;
    private List<SearchModel> searchModels;
    private List<SearchModel> filterSearchModels;

    public SearchAdapter(@NonNull Context context
            , @NonNull ItemSelectionListener listener) {
        this.context = context;
        this.listener = listener;
        searchModels = new ArrayList<>();
        filterSearchModels = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.aya_search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchModel model = filterSearchModels.get(position);
        String suraName = context.getResources().getStringArray(R.array.sura_name)[model.getSura() - 1];
        holder.ayaContentTextView.setText(model.getPure_text());
        holder.ayaNumTextView.setText(String.valueOf(model.getSura_aya()));
        holder.guz2NumTextView.setText(String.valueOf(model.getJuz()));
        holder.suraNameTextView.setText(suraName);
        holder.pageNumTextView.setText(String.valueOf(model.getPage()));
        holder.hizbNumTextView.setText(String.valueOf(model.getHezb()));
        holder.rub3NumTextView.setText(String.valueOf(model.getQuarter()));
        holder.itemView.setOnClickListener(v -> {
            listener.onSelectItem(model);
        });
    }

    @Override
    public int getItemCount() {
        return filterSearchModels.size();
    }

    public void setSearchModels(List<SearchModel> searchModels) {
        this.searchModels = searchModels;
        this.filterSearchModels = searchModels;
        notifyDataSetChanged();
    }

    public void filter(String inputQuery) {
        if (inputQuery.isEmpty()) {
            filterSearchModels = searchModels;
        } else {
            List<SearchModel> filteredList = new ArrayList<>();
            for (SearchModel row : searchModels) {
                if (row.getPure_text().toLowerCase().contains(inputQuery.toLowerCase())) {
                    filteredList.add(row);
                }
            }
            filterSearchModels = filteredList;
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_aya_content)
        TextView ayaContentTextView;
        @BindView(R.id.tv_aya_num)
        TextView ayaNumTextView;
        @BindView(R.id.tv_guz2_num)
        TextView guz2NumTextView;
        @BindView(R.id.tv_hizb_num)
        TextView hizbNumTextView;
        @BindView(R.id.tv_rub3_num)
        TextView rub3NumTextView;
        @BindView(R.id.tv_sura_name)
        TextView suraNameTextView;
        @BindView(R.id.tv_page_num)
        TextView pageNumTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
