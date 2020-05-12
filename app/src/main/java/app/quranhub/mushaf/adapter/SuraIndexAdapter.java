package app.quranhub.mushaf.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.quranhub.R;
import app.quranhub.databinding.SuraIndexRowBinding;
import app.quranhub.mushaf.listener.ItemSelectionListener;
import app.quranhub.mushaf.model.SuraIndexModelMapper;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SuraIndexAdapter extends RecyclerView.Adapter<SuraIndexAdapter.ViewHolder> {


    private List<SuraIndexModelMapper> suraIndexModelList;
    private List<SuraIndexModelMapper> suraIndexFiliterList;
    private Context context;
    private ItemSelectionListener<Integer> itemSelectionListener;

    public SuraIndexAdapter(Context context, ItemSelectionListener<Integer> itemSelectionListener) {
        this.context = context;
        suraIndexModelList = new ArrayList<>();
        suraIndexFiliterList = new ArrayList<>();
        this.itemSelectionListener = itemSelectionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SuraIndexRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.sura_index_row, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position >= suraIndexFiliterList.size())
            return;
        SuraIndexModelMapper suraIndexModel = suraIndexFiliterList.get(position);
        if (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            holder.leftBorder.setImageResource(R.drawable.gold_ornament_surah_ic);
            holder.rightBorder.setImageResource(R.drawable.gold_ornament_surah_mirror_ic);
            holder.suraName.setTypeface(ResourcesCompat.getFont(context, R.font.secondary_font), Typeface.BOLD);
            //holder.suraName.setTextSize(TypedValue.COMPLEX_UNIT_SP,context.getResources().getDimension(R.dimen.text_size_18));
        }

        holder.rowBinding.setSuraIndex(suraIndexModel);
        holder.itemView.setOnClickListener(v -> {
            itemSelectionListener.onSelectItem(Integer.valueOf(suraIndexModel.getPage()));
        });

    }


    @Override
    public int getItemCount() {
        return suraIndexFiliterList.size();
    }

    public void setSuraIndexModelList(List<SuraIndexModelMapper> suraIndexModelList) {
        this.suraIndexModelList = suraIndexModelList;
        this.suraIndexFiliterList = suraIndexModelList;
        notifyDataSetChanged();
    }

    public void filter(String inputQuery) {
        if (inputQuery.isEmpty()) {
            suraIndexFiliterList = suraIndexModelList;
        } else {
            List<SuraIndexModelMapper> filteredList = new ArrayList<>();
            for (SuraIndexModelMapper row : suraIndexModelList) {

                if (row.getName().toLowerCase().contains(inputQuery.toLowerCase())) {
                    filteredList.add(row);
                }
            }
            suraIndexFiliterList = filteredList;
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        SuraIndexRowBinding rowBinding;
        @BindView(R.id.right_sura_border)
        ImageView rightBorder;
        @BindView(R.id.left_sura_border)
        ImageView leftBorder;
        @BindView(R.id.sura_name_tv)
        TextView suraName;

        public ViewHolder(SuraIndexRowBinding rowBinding) {
            super(rowBinding.getRoot());
            this.rowBinding = rowBinding;
            ButterKnife.bind(this, rowBinding.getRoot());
        }
    }
}
