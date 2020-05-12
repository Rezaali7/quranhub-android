package app.quranhub.utils.interfaces;

public interface ToolbarActionsListener {

    void onNavDrawerClick();

    void onSuraClick();

    void onGuz2Click();

    void onBookmarkClick();

    void selectNavDrawerItem(long itemIdentifier, boolean fireOnClick);

}