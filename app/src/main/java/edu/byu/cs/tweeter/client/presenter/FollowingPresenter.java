package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.observer.PagedObserver;
import edu.byu.cs.tweeter.client.presenter.view.PagedView;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User> {

    public FollowingPresenter(PagedView<User> view) {
        super(view);
    }

    @Override
    public void load(User user) {
        followService.loadMoreFollowees(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, new GetFollowingObserver(this));
    }

    private class GetFollowingObserver extends PagedObserver<User> {
        public GetFollowingObserver(PagedPresenter<User> presenter) {
            super(presenter);
        }

        @Override
        protected void showError(String message) {
            view.displayMessage("Failed to get following: " + message);
        }

        @Override
        protected void showException(Exception ex) {
            view.displayMessage("Failed to get following because of exception: " + ex.getMessage());
        }
    }

}
