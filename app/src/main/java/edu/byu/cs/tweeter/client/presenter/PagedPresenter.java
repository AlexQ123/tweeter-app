package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.observer.GetUserObserver;
import edu.byu.cs.tweeter.client.presenter.view.PagedView;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter {

    protected static final int PAGE_SIZE = 10;

    protected PagedView<T> view;

    protected T lastItem;

    protected boolean hasMorePages;
    protected boolean isLoading = false;

    public PagedPresenter(PagedView<T> view) {
        this.view = view;
    }

    // Getters
    public PagedView<T> getView() {
        return view;
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    // Setters
    public void setLastItem(T lastItem) {
        this.lastItem = lastItem;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    // Other
    public void loadMoreItems(User user) {
        isLoading = true;
        view.setLoadingFooter(true);
        load(user);
    }

    protected abstract void load(User user);

    public void userClicked(String userAlias) {
        userService.userClicked(Cache.getInstance().getCurrUserAuthToken(), userAlias, new GetUserObserver<>(view));
    }

}
