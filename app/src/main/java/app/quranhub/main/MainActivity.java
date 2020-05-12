package app.quranhub.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mikepenz.materialdrawer.Drawer;

import app.quranhub.Constants;
import app.quranhub.audio_manager.AyaAudioService;
import app.quranhub.base.BaseActivity;
import app.quranhub.downloads_manager.DownloadsManagerActivity;
import app.quranhub.mushaf.fragments.BookmarksFragment;
import app.quranhub.mushaf.fragments.BooksLibraryFragment;
import app.quranhub.mushaf.fragments.Mus7fFragment;
import app.quranhub.mushaf.fragments.MyNotesFragment;
import app.quranhub.mushaf.fragments.PdfViewerFragment;
import app.quranhub.mushaf.fragments.SearchFragment;
import app.quranhub.mushaf.fragments.SubjectsFragment;
import app.quranhub.mushaf.fragments.SuraGuz2IndexFragment;
import app.quranhub.mushaf.fragments.TafseerFragment;
import app.quranhub.mushaf.fragments.TopicAyaFragment;
import app.quranhub.mushaf.listener.QuranNavigationCallbacks;
import app.quranhub.mushaf.model.TopicCategory;
import app.quranhub.settings.SettingsActivity;
import app.quranhub.R;
import app.quranhub.utils.DrawerUtil;
import app.quranhub.utils.PreferencesUtils;
import app.quranhub.utils.SharedPrefsUtil;
import app.quranhub.utils.interfaces.ToolbarActionsListener;

public class MainActivity extends BaseActivity
        implements ToolbarActionsListener, DrawerUtil.Mus7afDrawerItemClickListener, QuranNavigationCallbacks {

    private Drawer drawer;
    private String currentFragment;
    private Drawer.OnDrawerListener onDrawerListener;
    private boolean isDismissAllow = true;
    public static boolean isActivityActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        observeOnDrawerOpen();
        drawer = DrawerUtil.initDrawer(this, savedInstanceState, onDrawerListener);
        if (savedInstanceState == null) {
            launchMushafFragment();
        } else {
            setCurrentFragmentData(savedInstanceState.getString("fragment"));
        }
    }

    private void observeOnDrawerOpen() {

        onDrawerListener = new Drawer.OnDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                isDismissAllow = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isDismissAllow = true;
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                dismissAudioPopup();
            }
        };
    }

    private void dismissAudioPopup() {
        if(isDismissAllow && currentFragment.equals("mushaf")) {
            isDismissAllow = false;
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("Mushaf");
            if(fragment != null && fragment instanceof Mus7fFragment) {
                ((Mus7fFragment) fragment).dismissAudioPopup();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState = drawer.saveInstanceState(outState);
        outState.putString("fragment", currentFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityActive = false;
    }

    private void setCurrentFragmentData(String fragmentName) {
        currentFragment = fragmentName;
    }

    private void launchMushafFragment() {
        currentFragment = "mushaf";
        Mus7fFragment mus7fFragment;

        // get current aya id if Main activity is launched from audio notification OR launch app with audio notification app
        if((getIntent().getExtras() != null && getIntent().getExtras().getBoolean(AyaAudioService.FROM_NOTIFICATION)) || SharedPrefsUtil.getBoolean(this, AyaAudioService.SERVICE_RUNNING, false)) {
            int ayaId = SharedPrefsUtil.getInteger(this, AyaAudioService.AYA_ID_KEY, 1);
            mus7fFragment = Mus7fFragment.newNotifcationInstance(ayaId);
        }
        else if (PreferencesUtils.getLastReadPageSetting(this)) {
            int pageNumber = Constants.QURAN.NUM_OF_PAGES - SharedPrefsUtil.getInteger(this
                    , "last_open_page", Constants.QURAN.NUM_OF_PAGES - 1);
            mus7fFragment = Mus7fFragment.newInstance(pageNumber);
        } else {
            mus7fFragment = new Mus7fFragment();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, mus7fFragment, "Mushaf");
        transaction.commit();


    }

    // handle new intent get in stack single-top when click on audio notification
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if((getIntent().getExtras() != null && getIntent().getExtras().getBoolean(AyaAudioService.FROM_NOTIFICATION)) || SharedPrefsUtil.getBoolean(this, AyaAudioService.SERVICE_RUNNING, false)) {
            int ayaId = SharedPrefsUtil.getInteger(this, AyaAudioService.AYA_ID_KEY, 1);
            Mus7fFragment mus7fFragment = Mus7fFragment.newNotifcationInstance(ayaId);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, mus7fFragment, "Mushaf");
            transaction.commit();
        }
    }

    @Override
    public void onNavDrawerClick() {
        dismissAudioPopup();
        drawer.openDrawer();
    }

    @Override
    public void onSuraClick() {
        openIndex(SuraGuz2IndexFragment.SURA_INDEX_TAB);
        selectNavDrawerItem(DrawerUtil.IDENTIFIER_INDEX, false);
    }

    @Override
    public void onGuz2Click() {
        openIndex(SuraGuz2IndexFragment.GUZ2_INDEX_TAB);
        selectNavDrawerItem(DrawerUtil.IDENTIFIER_INDEX, false);
    }

    @Override
    public void onBookmarkClick() {
        openBookmarks();
        selectNavDrawerItem(DrawerUtil.IDENTIFIER_BOOKMARKS, false);
    }

    @Override
    public void selectNavDrawerItem(long itemIdentifier, boolean fireOnClick) {
        if (drawer.getCurrentSelection() == itemIdentifier)
            return;
        drawer.setSelection(itemIdentifier, fireOnClick);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else if (currentFragment.equals("pdf_viewer")) {
            super.onBackPressed();
            currentFragment = "translation";
        } else if (!currentFragment.equals("mushaf")) {
            backToMushaf();
        } else {
            super.onBackPressed();
        }
    }



    private void backToMushaf() {
        int lastOpenedPage = Constants.QURAN.NUM_OF_PAGES - SharedPrefsUtil.getInteger(this
                , "last_open_page", Constants.QURAN.NUM_OF_PAGES - 1);
        currentFragment = "mushaf";
        gotoQuranPage(lastOpenedPage);
    }


    @Override
    public void openIndex(int indexTab) {
        checkPrevFragment();
        drawer.closeDrawer();
        SuraGuz2IndexFragment suraGuz2IndexFragment =
                SuraGuz2IndexFragment.newInstance(indexTab);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, suraGuz2IndexFragment, "index");
        transaction.commit();
        currentFragment = "index";
    }

    @Override
    public void openTopics() {
        checkPrevFragment();
        SubjectsFragment subjectsFragment = new SubjectsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, subjectsFragment);
        transaction.commit();
        currentFragment = "subjects";
    }

    @Override
    public void openLibrary() {
        checkPrevFragment();
        BooksLibraryFragment fragment = new BooksLibraryFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
        currentFragment = "library";
    }

    @Override
    public void openBookmarks() {
        checkPrevFragment();
        drawer.closeDrawer();
        BookmarksFragment bookmarksFragment =
                BookmarksFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, bookmarksFragment);
        transaction.commit();
        currentFragment = "bookmark";
    }

    private void checkPrevFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (currentFragment.equals("pdf_viewer")) {
            fragmentManager.popBackStack();
        }
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void openMyNotes() {
        checkPrevFragment();
        MyNotesFragment fragment = new MyNotesFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
        currentFragment = "notes";
    }

    public void openPdfFragment(String fileName) {
        checkPrevFragment();
        Bundle bundle = new Bundle();
        bundle.putString("file_name", fileName);
        PdfViewerFragment pdfViewerFragment = new PdfViewerFragment();
        pdfViewerFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, pdfViewerFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        currentFragment = "pdf_viewer";
    }

    @Override
    public void openSettings() {
        checkPrevFragment();
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @Override
    public void openDownloadsManager() {
        checkPrevFragment();
        startActivity(new Intent(this, DownloadsManagerActivity.class));
    }

    @Override
    public void openMushaf() {
        drawer.closeDrawer();
        if (!currentFragment.equals("mushaf")) {
            checkPrevFragment();
            backToMushaf();
        }
    }

    public void openTafseerScreen(String suraName, int suraNumber, String bookDbName, String bookName, int ayaNumber) {
        checkPrevFragment();
        currentFragment = "tafseer";
        TafseerFragment tafseerFragment = TafseerFragment.newInstance(suraName, suraNumber, bookDbName, bookName, ayaNumber);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, tafseerFragment);
        transaction.commit();
    }

    public void openSearchFragment() {
        checkPrevFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        currentFragment = "search";
        SearchFragment fragment = new SearchFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();

    }

    public void openTopicAyasFragment(TopicCategory category) {
        checkPrevFragment();
        currentFragment = "tafseer";
        TopicAyaFragment fragment = TopicAyaFragment.getInstance(category);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void openTafseerScreen(String bookDbName, String bookName) {
        openTafseerScreen(getResources().getStringArray(R.array.sura_name)[0]
                , 1, bookDbName, bookName, 1);
    }

    @Override
    public void gotoQuranPage(int pageNumber) {
        currentFragment = "mushaf";
        Mus7fFragment mus7fFragment = Mus7fFragment.newInstance(pageNumber);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, mus7fFragment, "Mushaf");
        transaction.commit();
        selectNavDrawerItem(DrawerUtil.IDENTIFIER_MUSHAF, false);
    }

    @Override
    public void gotoQuranPageAya(int pageNumber, int ayaId, boolean addToStack) {

        Mus7fFragment mus7fFragment = Mus7fFragment.newInstance(pageNumber, ayaId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, mus7fFragment, "Mushaf");
        if(addToStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
        selectNavDrawerItem(DrawerUtil.IDENTIFIER_MUSHAF, false);
        currentFragment = "mushaf";
    }

}
