package app.quranhub.mushaf.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.File;

import app.quranhub.Constants;
import app.quranhub.R;
import butterknife.BindView;
import butterknife.ButterKnife;


public class PdfViewerFragment extends Fragment {

    @BindView(R.id.pdfView)
    PDFView pdfView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private String fileName;
    private Uri uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_viewer, container, false);
        ButterKnife.bind(this, view);
        setPdfView();
        return view;
    }

    private void setPdfView() {
        OnLoadCompleteListener completeListener = nbPages -> progressBar.setVisibility(View.GONE);
        OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
            @Override
            public void onPageChanged(int page, int pageCount) {
                Log.d("TAG", "onPageChanged: " + page);
            }
        };
        fileName = getArguments().getString("file_name");
        File file = new File(Environment.getExternalStorageDirectory() + File.separator
                + Constants.DIRECTORY.LIBRARY_PUBLIC, fileName);

        pdfView.fromFile(file)
                .enableDoubletap(true)
                .enableSwipe(true)
                .onLoad(completeListener)
                .onPageChange(pageChangeListener)
                .load();

    }

}
