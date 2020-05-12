package app.quranhub.mushaf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.quranhub.R;
import app.quranhub.mushaf.model.TafseerModel;
import app.quranhub.utils.PreferencesUtils;
import at.blogc.android.views.ExpandableTextView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TafseerAdapter extends RecyclerView.Adapter<TafseerAdapter.ViewHolder> {

    private Context context;
    private List<TafseerModel> tafseerModelList;
    private List<TafseerModel> tafseerFilteredModelList;


    public TafseerAdapter(Context context) {
        this.context = context;
        tafseerModelList = new ArrayList<>();
        tafseerFilteredModelList = new ArrayList<>();
    }

    public void setTafseerModelList(List<TafseerModel> tafseerModelList) {
        this.tafseerModelList = tafseerModelList;
        this.tafseerFilteredModelList = tafseerModelList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tafseer_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        TafseerModel model = tafseerFilteredModelList.get(position);
        holder.ayaTv.setText(model.getText());
        holder.tafseerTv.setText(model.getTafseer());
        holder.linesTv.setText(model.getTafseer());

        if (model.isExpandable()) {
            holder.tafseerTv.expand();
        } else {
            holder.tafseerTv.collapse();
        }


        holder.linesTv.post(() -> {
            final int lineCount = holder.linesTv.getLineCount();
            holder.linesTv.setVisibility(View.GONE);
            if (lineCount < 5) {
                holder.moreTv.setVisibility(View.GONE);
            } else {
                holder.moreTv.setVisibility(View.VISIBLE);
            }
        });


        if (!PreferencesUtils.getAppLangSetting(context).equals("ar") && !PreferencesUtils.getQuranTranslationLanguage(context).equals("ar")) {             // !LocaleUtil.getTranslationLanguage().equals("ar")
            holder.parentLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        holder.moreTv.setOnClickListener(v -> {
            if (holder.tafseerTv.isExpanded()) {
                holder.moreTv.setText(context.getString(R.string.more));
                holder.tafseerTv.collapse();
                tafseerFilteredModelList.get(position).setExpandable(false);
            } else {
                holder.moreTv.setText(context.getString(R.string.collapse));
                holder.tafseerTv.expand();
                tafseerFilteredModelList.get(position).setExpandable(true);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tafseerFilteredModelList.size();
    }

    public void filter(String inputQuery) {
        if (inputQuery.isEmpty()) {
            tafseerFilteredModelList = tafseerModelList;
        } else {
            List<TafseerModel> filteredList = new ArrayList<>();
            for (TafseerModel row : tafseerModelList) {
                if (row.getPure_text().toLowerCase().contains(inputQuery.toLowerCase())) {
                    filteredList.add(row);
                }
            }
            tafseerFilteredModelList = filteredList;
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tafseer_tv)
        ExpandableTextView tafseerTv;
        @BindView(R.id.aya_tv)
        TextView ayaTv;
        @BindView(R.id.more_tv)
        TextView moreTv;
        @BindView(R.id.numlines_tv)
        TextView linesTv;
        @BindView(R.id.parent_layout)
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


    }
}
