package app.quranhub.mushaf.fragments;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import app.quranhub.R;
import app.quranhub.mushaf.adapter.NotesAdapter;
import app.quranhub.mushaf.data.entity.Note;
import app.quranhub.mushaf.dialogs.AddNoteDialog;
import app.quranhub.mushaf.dialogs.NotesFilterDialog;
import app.quranhub.mushaf.listener.ItemSelectionListener;
import app.quranhub.mushaf.listener.QuranNavigationCallbacks;
import app.quranhub.mushaf.model.DisplayedNote;
import app.quranhub.mushaf.viewmodel.NotesViewModel;
import app.quranhub.utils.ScreenUtil;
import app.quranhub.utils.interfaces.ToolbarActionsListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MyNotesFragment extends Fragment implements NotesAdapter.NoteCallback, AddNoteDialog.AddNoteListener, ItemSelectionListener<Integer> {

    @BindView(R.id.ib_finish_edit)
    ImageButton finishEditImageButton;
    @BindView(R.id.edit_btn)
    ImageButton editButton;
    @BindView(R.id.filter_btn)
    ImageButton filterButton;
    @BindView(R.id.et_search)
    EditText searchEt;
    @BindView(R.id.notes_rv)
    RecyclerView notesRv;
    @BindView(R.id.progrees_bar)
    ProgressBar progressBar;
    @BindView(R.id.no_notes_tv)
    TextView noNotesTv;


    private String inputSearch = "";
    private ToolbarActionsListener navDrawerListener;
    private QuranNavigationCallbacks quranNavigationCallbacks;
    private NotesAdapter adapter;
    private NotesViewModel viewModel;
    private int selectedFilterNoteType = 0;
    private boolean openDialog = false;
    private DisplayedNote selectedAyaNote;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ToolbarActionsListener) {
            navDrawerListener = (ToolbarActionsListener) context;
        }
        if (context instanceof QuranNavigationCallbacks) {
            quranNavigationCallbacks = (QuranNavigationCallbacks) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_notes, container, false);
        ButterKnife.bind(this, view);
        if (savedInstanceState != null) {
            getPrevState(savedInstanceState);
        }
        initViews();
        bindViewModel();
        observeSearchInput();
        return view;
    }

    private void getPrevState(Bundle savedInstanceState) {
        inputSearch = savedInstanceState.getString("input_search");
        selectedFilterNoteType = savedInstanceState.getInt("filter_type");
        openDialog = savedInstanceState.getBoolean("open_dialog");
        if (openDialog) {
            openDialog = false;
            selectedAyaNote = savedInstanceState.getParcelable("selected_note");
            openAddNoteDialog(selectedAyaNote);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("input_search", inputSearch);
        outState.putInt("filter_type", selectedFilterNoteType);
        outState.putBoolean("open_dialog", openDialog);
        outState.putParcelable("selected_note", selectedAyaNote);
    }

    private void bindViewModel() {
        viewModel = new ViewModelProvider(this).get(NotesViewModel.class);
        viewModel.getAllNotes();
        viewModel.getNotes().observe(getViewLifecycleOwner(), displayedNotes -> {
            progressBar.setVisibility(View.GONE);
            if (displayedNotes != null && displayedNotes.size() > 0) {
                adapter.setNoteList(displayedNotes);
                if (inputSearch != null && !TextUtils.isEmpty(inputSearch.trim())) {
                    adapter.filter(inputSearch);
                }
                if (selectedFilterNoteType != 0) {
                    adapter.setFilteredNotes(selectedFilterNoteType - 1);
                }
            } else {
                noNotesTv.setVisibility(View.VISIBLE);
            }
        });
    }

    private void observeSearchInput() {
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
                inputSearch = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initViews() {
        adapter = new NotesAdapter(getActivity(), this);
        notesRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        notesRv.setAdapter(adapter);
    }

    @OnClick(R.id.hamburger_iv)
    void onNavHamburgerClick() {
        navDrawerListener.onNavDrawerClick();
    }

    @Override
    public void onNavigateToAya(int ayaId, int pageNum) {
        ScreenUtil.dismissKeyboard(getContext(), searchEt);
        quranNavigationCallbacks.gotoQuranPageAya(pageNum, ayaId, false);
    }

    /*
     Get displayedNote => if = null so this aya not has note before, else the aya has note before
     */
    @Override
    public void onGetNoteDetails(DisplayedNote displayedNote) {
        if (ScreenUtil.getOrientationState(getActivity()) == ScreenUtil.PORTRAIT_STATE) {
            openAddNoteDialog(displayedNote);
        } else {
            openDialog = true;
            selectedAyaNote = displayedNote;
        }
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void openAddNoteDialog(DisplayedNote displayedNote) {
        Note note = new Note(displayedNote.getAyaId(), displayedNote.getNoteType(), displayedNote.getNoteText(), displayedNote.getNoteRecorderPath());
        AddNoteDialog dialog = AddNoteDialog.getInstance(note);
        dialog.show(getChildFragmentManager(), "AddNoteDialog");
    }

    @Override
    public void onDeleteNote(int ayaId) {
        viewModel.deleteNote(ayaId);
    }

    @Override
    public void onAddNote(Note note, boolean isEditable) {
        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        Toast.makeText(getActivity(), getString(R.string.note_edited), Toast.LENGTH_LONG).show();
        adapter.updateNoteType(note);
        viewModel.updateNote(note);
    }

    @Override
    public void onDismissDialog() {
        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @OnClick(R.id.edit_btn)
    public void onNoteEdit() {
        adapter.setEditable(true);
        finishEditImageButton.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.GONE);
        filterButton.setVisibility(View.GONE);
    }

    @OnClick(R.id.filter_btn)
    public void onClickFilter() {
        NotesFilterDialog filterDialog = NotesFilterDialog.getInstance(selectedFilterNoteType);
        filterDialog.show(getChildFragmentManager(), "NotesFilterDialog");
    }

    @OnClick(R.id.ib_finish_edit)
    public void onFinishEdit() {
        adapter.setEditable(false);
        finishEditImageButton.setVisibility(View.GONE);
        editButton.setVisibility(View.VISIBLE);
        filterButton.setVisibility(View.VISIBLE);
    }


    @Override
    public void onSelectItem(Integer noteFilterType) {
        selectedFilterNoteType = noteFilterType;
        if (noteFilterType == 0) {
            adapter.setAllNotes();
        } else {
            adapter.setFilteredNotes(selectedFilterNoteType - 1);
        }

    }
}
