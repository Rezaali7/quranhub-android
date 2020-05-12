package app.quranhub.mushaf.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.quranhub.R;
import app.quranhub.mushaf.model.HizbQuarterDataModel;
import app.quranhub.utils.LocaleUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Guz2IndexAdapter extends RecyclerView.Adapter<Guz2IndexAdapter.ViewHolder> implements Filterable {

    private static final String TAG = Guz2IndexAdapter.class.getSimpleName();

    public static final int FILTER_GUZ2_ALL = 0;

    @Nullable
    private List<HizbQuarterDataModel> filteredHizbQuarterDataModels;
    @Nullable
    private List<HizbQuarterDataModel> originalHizbQuarterDataModels;
    @Nullable
    private IndexItemClickListener clickCallback;
    private Context context;
    private int filterGuz2 = FILTER_GUZ2_ALL;


    public Guz2IndexAdapter(@Nullable List<HizbQuarterDataModel> hizbQuarterDataModels
            , @Nullable IndexItemClickListener clickListener) {
        this.filteredHizbQuarterDataModels = hizbQuarterDataModels;
        this.originalHizbQuarterDataModels = hizbQuarterDataModels;
        this.clickCallback = clickListener;
    }

    public Guz2IndexAdapter(@Nullable List<HizbQuarterDataModel> hizbQuarterDataModels
            , int filterGuz2, @Nullable IndexItemClickListener clickListener) {
        this.filteredHizbQuarterDataModels = hizbQuarterDataModels;
        this.originalHizbQuarterDataModels = hizbQuarterDataModels;
        this.filterGuz2 = filterGuz2;
        this.clickCallback = clickListener;

        if (hizbQuarterDataModels != null && filterGuz2 != FILTER_GUZ2_ALL) {
            getFilter().filter(Integer.toString(filterGuz2));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_guz2_index, parent, false);

        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HizbQuarterDataModel model = filteredHizbQuarterDataModels.get(position);

        if (model.getQuarter() == 1) {
            holder.headerLayout.setVisibility(View.VISIBLE);
            holder.headerGuz2TextView.setText(
                    context.getResources().getStringArray(R.array.agza2_name)[model.getJuz() - 1]);
            holder.headerHizbTextView.setText(
                    context.getResources().getStringArray(R.array.hezb_name)[model.getHizb() - 1]);
        } else {
            holder.headerLayout.setVisibility(View.GONE);
        }

        switch (model.getQuarter()) {
            case 1:
                holder.quarterIndicatorImageView.setImageResource(R.drawable.juz2_0);
                break;
            case 2:
                holder.quarterIndicatorImageView.setImageResource(R.drawable.juz2_1_4);
                break;
            case 3:
                holder.quarterIndicatorImageView.setImageResource(R.drawable.juz2_1_2);
                break;
            case 4:
                holder.quarterIndicatorImageView.setImageResource(R.drawable.juz2_3_4);
                break;
        }

        holder.ayaContentTextView.setText(model.getAyaText());
        holder.rub3NumTextView.setText(LocaleUtil.formatNumber(model.getQuarter()));
        holder.suraNameTextView.setText(
                context.getResources().getStringArray(R.array.sura_name)[model.getSuraNumber() - 1]);
        holder.ayaNumTextView.setText(LocaleUtil.formatNumber(model.getAyaNumber()));
        holder.startPageNumTextView.setText(LocaleUtil.formatNumber(model.getStartPage()));
        holder.endPageNumTextView.setText(LocaleUtil.formatNumber(model.getEndPage()));
    }

    @Override
    public int getItemCount() {
        if (filteredHizbQuarterDataModels == null) {
            return 0;
        }
        return filteredHizbQuarterDataModels.size();
    }

    public void setHizbQuarterDataModels(@Nullable List<HizbQuarterDataModel> hizbQuarterDataModels) {
        this.originalHizbQuarterDataModels = hizbQuarterDataModels;
        getFilter().filter(Integer.toString(filterGuz2));
    }

    @Nullable
    public List<HizbQuarterDataModel> getHizbQuarterDataModels() {
        return filteredHizbQuarterDataModels;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (originalHizbQuarterDataModels == null) {
                    return null;
                }

                List<HizbQuarterDataModel> resultHizbQuarterDataModels = new ArrayList<>();
                int guz2 = Integer.parseInt(constraint.toString());

                if (guz2 == FILTER_GUZ2_ALL) {
                    resultHizbQuarterDataModels = originalHizbQuarterDataModels;
                } else {
                    for (HizbQuarterDataModel quarterDataModel : originalHizbQuarterDataModels) {
                        if (quarterDataModel.getJuz() == guz2) {
                            resultHizbQuarterDataModels.add(quarterDataModel);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = resultHizbQuarterDataModels;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterGuz2 = Integer.parseInt(constraint.toString());
                Log.d(TAG, "publishResults: filterGuz2 = " + filterGuz2);
                if (results != null) {
                    filteredHizbQuarterDataModels = (List<HizbQuarterDataModel>) results.values;
                    notifyDataSetChanged();
                }
            }
        };
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.header)
        ConstraintLayout headerLayout;
        @BindView(R.id.tv_header_guz2)
        TextView headerGuz2TextView;
        @BindView(R.id.tv_header_hizb)
        TextView headerHizbTextView;
        @BindView(R.id.iv_quarter_indicator)
        ImageView quarterIndicatorImageView;
        @BindView(R.id.tv_aya_content)
        TextView ayaContentTextView;
        @BindView(R.id.tv_rub3_num)
        TextView rub3NumTextView;
        @BindView(R.id.tv_sura_name)
        TextView suraNameTextView;
        @BindView(R.id.tv_aya_num)
        TextView ayaNumTextView;
        @BindView(R.id.tv_page_num_start)
        TextView startPageNumTextView;
        @BindView(R.id.tv_page_num_end)
        TextView endPageNumTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickCallback != null) {
                int clickedItemIndex = getAdapterPosition();
                clickCallback.onIndexItemClick(filteredHizbQuarterDataModels.get(clickedItemIndex)
                        , originalHizbQuarterDataModels.indexOf(filteredHizbQuarterDataModels.get(clickedItemIndex)));
            }
        }
    }


    /**
     * Used in handling items clicks
     */
    public interface IndexItemClickListener {
        void onIndexItemClick(HizbQuarterDataModel model, int clickedItemIndex);
    }

}
