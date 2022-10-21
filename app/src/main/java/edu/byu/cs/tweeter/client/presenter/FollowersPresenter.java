package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.observer.PagedObserver;
import edu.byu.cs.tweeter.client.presenter.view.PagedView;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User> {

    public FollowersPresenter(PagedView<User> view) {
        super(view);
    }

    @Override
    public void load(User user) {
        followService.loadMoreFollowers(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, new GetFollowersObserver(this));
    }

    private class GetFollowersObserver extends PagedObserver<User> {
        public GetFollowersObserver(PagedPresenter<User> presenter) {
            super(presenter);
        }

        @Override
        protected void showError(String message) {
            view.displayMessage("Failed to get followers: " + message);
        }

        @Override
        protected void showException(Exception ex) {
            view.displayMessage("Failed to get followers because of exception: " + ex.getMessage());
        }
    }

}
