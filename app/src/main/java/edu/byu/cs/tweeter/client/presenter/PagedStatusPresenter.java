package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.observer.GetUserObserver;
import edu.byu.cs.tweeter.client.presenter.view.PagedStatusView;

public abstract class PagedStatusPresenter<T> extends PagedPresenter<T> {

    protected PagedStatusView<T> view;

    public PagedStatusPresenter(PagedStatusView<T> view) {
        super(view);
        this.view = view;
    }

    public void clickableClicked(String clickable) {
        if (clickable.contains("http")) {
            view.startExternal(clickable);
        }
        else {
            userService.userClicked(Cache.getInstance().getCurrUserAuthToken(), clickable, new GetUserObserver<>(view));
        }
    }

}
