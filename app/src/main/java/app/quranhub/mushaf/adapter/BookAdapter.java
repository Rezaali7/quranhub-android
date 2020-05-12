package app.quranhub.mushaf.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.quranhub.R;
import app.quranhub.mushaf.data.entity.Book;
import app.quranhub.mushaf.listener.ItemSelectionListener;
import app.quranhub.mushaf.network.model.BookContent;
import app.quranhub.utils.LocaleUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private List<BookContent> bookList;
    private List<BookContent> translationFiliterList;
    public static final int TRANSLATION_NOT_DOWNLOADED = 0;
    public static final int TRANSLATION_DOWNLOADED_IN_PROGRESS = 1;
    public static final int TRANSLATION_DOWNLOADED = 2;
    private boolean isEditable;
    private TranslationActionsListener translationActionsListener;

    public BookAdapter(TranslationActionsListener translationActionsListener) {
        this.translationActionsListener = translationActionsListener;
        isEditable = false;
        bookList = new ArrayList<>();
        translationFiliterList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_row, parent, false);
        return new BookAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookContent model = translationFiliterList.get(position);
        holder.translateName.setText(model.getName());
        if (isEditable) {
            holder.translateIcon.setImageResource(R.drawable.ic_delete);
            changeIconType(false, holder);
        } else if (model.getDownloadStatus() == TRANSLATION_DOWNLOADED) {
            holder.translateIcon.setImageResource(R.drawable.check_gold_ic);
            changeIconType(false, holder);
        } else if (model.getDownloadStatus() == TRANSLATION_NOT_DOWNLOADED) {
            holder.translateIcon.setImageResource(R.drawable.download_action_green_ic);
            changeIconType(false, holder);
        } else if (model.getDownloadStatus() == TRANSLATION_DOWNLOADED_IN_PROGRESS) {
            changeIconType(true, holder);
        }

        if (!LocaleUtil.getAppLanguage().equals("ar")) {
            holder.translateName.setGravity(Gravity.LEFT);
        }

        holder.translateName.setOnClickListener(v -> {
            if (model.getDownloadStatus() == TRANSLATION_NOT_DOWNLOADED) {                             // open file in pdf if it exist in local storage and downloaded before
                translationActionsListener.onDownloadTranslation(model);
            } else if (model.getDownloadStatus() == TRANSLATION_DOWNLOADED) {
                translationActionsListener.onSelectItem(model);
            }
        });

        holder.progressBar.setOnClickListener(v -> {
            translationActionsListener.onCancelDownload(model);
        });

        holder.translateIcon.setOnClickListener(v -> {
            if (isEditable) {
                translationActionsListener.onDeleteTranslation(model);
            } else if (model.getDownloadStatus() == TRANSLATION_NOT_DOWNLOADED) {
                translationActionsListener.onDownloadTranslation(model);
            }
        });
    }

    private void changeIconType(boolean isDownloadProgress, ViewHolder holder) {
        if (isDownloadProgress) {
            holder.cancelIcon.setVisibility(View.VISIBLE);
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.translateIcon.setVisibility(View.INVISIBLE);
        } else {
            holder.cancelIcon.setVisibility(View.INVISIBLE);
            holder.progressBar.setVisibility(View.INVISIBLE);
            holder.translateIcon.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return translationFiliterList.size();
    }

    public void setBookList(List<BookContent> bookList) {
        this.bookList = bookList;
        this.translationFiliterList = bookList;
        notifyDataSetChanged();
    }

    public void filter(String inputQuery) {
        if (inputQuery.isEmpty()) {
            if (!isEditable)
                translationFiliterList = bookList;
            else
                setDownloadTranslations();
        } else {
            List<BookContent> filteredList = new ArrayList<>();
            for (BookContent row : bookList) {
                if (isEditable && row.getDownloadStatus() != TRANSLATION_DOWNLOADED)
                    continue;
                if (row.getName().toLowerCase().contains(inputQuery.toLowerCase())) {
                    filteredList.add(row);
                }
            }
            translationFiliterList = filteredList;
        }
        notifyDataSetChanged();
    }


    public void setDownloadTranslations() {
        isEditable = true;
        List<BookContent> filteredList = new ArrayList<>();
        for (BookContent model : bookList) {
            if (model.getDownloadStatus() == TRANSLATION_DOWNLOADED)
                filteredList.add(model);
        }

        this.translationFiliterList = filteredList;
        notifyDataSetChanged();
    }

    public void setAllTranslation() {
        isEditable = false;
        translationFiliterList = bookList;
        notifyDataSetChanged();
    }

    public void removeDeletedFile(int id) {
        for (BookContent model : translationFiliterList) {
            if (model.getId() == id) {
                translationFiliterList.remove(model);
                break;
            }
        }

        for (BookContent model : bookList) {
            if (model.getId() == id) {
                model.setDownloadStatus(TRANSLATION_NOT_DOWNLOADED);
                break;
            }
        }

        notifyDataSetChanged();
    }

    public void updateBooksDownloadStatus(List<Book> models) {
        for (Book book : models) {
            for (BookContent content : translationFiliterList) {
                if (book.getId() == content.getId()) {
                    content.setDownloadId(book.getDownloadId());
                    content.setDownloadStatus(book.getDownloadStatus());
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.translation_tv)
        TextView translateName;
        @BindView(R.id.translation_iv)
        ImageView translateIcon;
        @BindView(R.id.cancel_download)
        ImageView cancelIcon;
        @BindView(R.id.download_progress)
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface TranslationActionsListener extends ItemSelectionListener<BookContent> {
        void onCancelDownload(BookContent model);

        void onDownloadTranslation(BookContent model);

        void onDeleteTranslation(BookContent model);
    }


}
