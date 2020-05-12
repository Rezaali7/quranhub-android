package app.quranhub.base;

public interface BasePresenter<T extends BaseView> {

    void onAttach(T view);

    void onDetach();

    boolean isViewAttached();

}
