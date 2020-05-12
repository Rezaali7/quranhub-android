package app.quranhub.mushaf.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.quranhub.Constants;
import app.quranhub.R;
import app.quranhub.mushaf.listener.ItemSelectionListener;
import app.quranhub.mushaf.model.DisplayableBookmark;
import app.quranhub.utils.LocaleUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.ViewHolder>
        implements Filterable {

    private static final String TAG = BookmarksAdapter.class.getSimpleName();

    @Nullable
    private List<DisplayableBookmark> originalBookmarks;
    @Nullable
    private List<DisplayableBookmark> filteredBookmarks;
    @NonNull
    private Context context;
    @NonNull
    private BookmarkActionListener bookmarkActionListener;
    protected int[] bookmarkColors;
    private String searchText = "";

    private boolean isEditable = false;

    public BookmarksAdapter(@NonNull Context context
            , @NonNull BookmarkActionListener bookmarkActionListener) {
        this.context = context;
        this.bookmarkActionListener = bookmarkActionListener;
        bookmarkColors = context.getResources().getIntArray(R.array.bookmark_colors);
    }

    public void setBookmarks(@NonNull List<DisplayableBookmark> bookmarks) {
        this.originalBookmarks = bookmarks;
        getFilter().filter(searchText);
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
        notifyDataSetChanged();
    }

    public boolean isEditable() {
        return this.isEditable;
    }

    public void toggleEdit() {
        setEditable(!isEditable);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_bookmarked_favorite_aya, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.bind(filteredBookmarks.get(position));
    }

    @Override
    public int getItemCount() {
        if (filteredBookmarks == null)
            return 0;
        return filteredBookmarks.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<DisplayableBookmark> resultBookmarks = new ArrayList<>();
                if (constraint.length() == 0) {
                    resultBookmarks = originalBookmarks;
                } else {
                    for (DisplayableBookmark bookmark : originalBookmarks) {
                        if (bookmark.getAyaContent().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            resultBookmarks.add(bookmark);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = resultBookmarks;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                searchText = constraint.toString();
                filteredBookmarks = (List<DisplayableBookmark>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    public void deleteBookmark(int ayaId) {
        for (DisplayableBookmark displayableBookmark : filteredBookmarks) {
            if (displayableBookmark.getAyaId() == ayaId) {
                originalBookmarks.remove(displayableBookmark);
                filteredBookmarks.remove(displayableBookmark);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void filterBookmarks(int bookmarkType) {
        if (bookmarkType == 0) { // show all bookmark
            filteredBookmarks = originalBookmarks;
        } else {
            List<DisplayableBookmark> filteredList = new ArrayList<>();
            for (DisplayableBookmark bookmark : originalBookmarks) {
                if (bookmark.getBookmarkType() == bookmarkType) {
                    filteredList.add(bookmark);
                }
                filteredBookmarks = filteredList;
            }
        }
        notifyDataSetChanged();
    }

    public void editBookmark(int ayaId, int bookmarkType, int colorIndex) {
        for (DisplayableBookmark displayableBookmark : filteredBookmarks) {
            if (displayableBookmark.getAyaId() == ayaId) {
                displayableBookmark.setBookmarkType(bookmarkType);
                displayableBookmark.setColorIndex(colorIndex);
                break;
            }
        }
        for (DisplayableBookmark displayableBookmark : originalBookmarks) {
            if (displayableBookmark.getAyaId() == ayaId) {
                displayableBookmark.setBookmarkType(bookmarkType);
                displayableBookmark.setColorIndex(colorIndex);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View rootView;

        @BindView(R.id.iv_bookmark_type)
        ImageView bookmarkTypeImageView;
        @BindView(R.id.tv_aya_content)
        TextView ayaContentTextView;
        @BindView(R.id.tv_aya_num)
        TextView ayaNumTextView;
        @BindView(R.id.ib_delete_bookmark)
        ImageButton deleteBookmarkImageButton;
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

        public ViewHolder(View view) {
            super(view);
            this.rootView = view;
            ButterKnife.bind(this, view);
        }

        @SuppressLint("SetTextI18n")
        public void bind(DisplayableBookmark displayableBookmark) {
            if (displayableBookmark.getBookmarkType() == Constants.BOOKMARK_TYPE.NOTE) {
                bookmarkTypeImageView.setColorFilter(null);
                bookmarkTypeImageView.setImageResource(R.drawable.bookmark_green);
            } else if (displayableBookmark.getBookmarkType() == Constants.BOOKMARK_TYPE.MEMORIZE) {
                bookmarkTypeImageView.setColorFilter(null);
                bookmarkTypeImageView.setImageResource(R.drawable.bookmark_red);
            } else if (displayableBookmark.getBookmarkType() == Constants.BOOKMARK_TYPE.RECITING) {
                bookmarkTypeImageView.setColorFilter(null);
                bookmarkTypeImageView.setImageResource(R.drawable.bookmark_gold);
            } else if (displayableBookmark.getBookmarkType() == Constants.BOOKMARK_TYPE.FAVORITE) {
                bookmarkTypeImageView.setColorFilter(null);
                bookmarkTypeImageView.setImageResource(R.drawable.fav_added__gold_ic);
            } else {
                bookmarkTypeImageView.setImageResource(R.drawable.bookmark_gold);
                bookmarkTypeImageView.setColorFilter(bookmarkColors[displayableBookmark.getColorIndex()]);
            }

            ayaContentTextView.setText(displayableBookmark.getAyaContent());
            ayaNumTextView.setText(LocaleUtil.formatNumber(displayableBookmark.getSuraAyaNumber()));
            guz2NumTextView.setText(LocaleUtil.formatNumber(displayableBookmark.getGuz2Number()));
            hizbNumTextView.setText(LocaleUtil.formatNumber(displayableBookmark.getHizbNumber()));
            rub3NumTextView.setText(LocaleUtil.formatNumber(displayableBookmark.getRub3Number()));
            suraNameTextView.setText(displayableBookmark.getSuraName());
            suraNameTextView.setTypeface(Typeface.create(Typeface.createFromAsset(
                    context.getAssets(), "fonts/diwany_thuluth.ttf"), Typeface.BOLD));
            pageNumTextView.setText(LocaleUtil.formatNumber(displayableBookmark.getPageNumber()));

            if (isEditable) {
                deleteBookmarkImageButton.setVisibility(View.VISIBLE);
            } else {
                deleteBookmarkImageButton.setVisibility(View.GONE);
            }

        }

        @OnClick(R.id.item_view)
        void gotoBookmarkAya() {
            if (!isEditable) {
                bookmarkActionListener.onSelectItem(filteredBookmarks.get(getAdapterPosition()));
            }
        }

        @OnClick(R.id.ib_delete_bookmark)
        void deleteBookmark() {
            if (isEditable) {
                Log.d(TAG, "delete bookmark: " + getAdapterPosition());
                bookmarkActionListener.deleteBookmark(filteredBookmarks.get(getAdapterPosition()));
            }
        }

        @OnClick(R.id.iv_bookmark_type)
        void displayBookmarkTypeDialog() {
            if (isEditable) {
                bookmarkActionListener.updateBookmarkType(filteredBookmarks.get(getAdapterPosition()).getBookmarkId());

                /*BookmarkTypesPopup bookmarkTypesPopup = new BookmarkTypesPopup(context, bookmarkTypeId -> {
                    bookmarkActionListener.updateBookmarkType(filteredBookmarks.get(getAdapterPosition())
                            , bookmarkTypeId);
                });
                bookmarkTypesPopup.showPopup(view);*/
            }
        }

    }

    public interface BookmarkActionListener extends ItemSelectionListener<DisplayableBookmark> {

        void deleteBookmark(@NonNull DisplayableBookmark displayableBookmark);

        void updateBookmarkType(@NonNull int bookmarkId);

    }
}
