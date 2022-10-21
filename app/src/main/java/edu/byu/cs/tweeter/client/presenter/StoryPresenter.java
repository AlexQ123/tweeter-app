package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.observer.PagedObserver;
import edu.byu.cs.tweeter.client.presenter.view.PagedStatusView;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedStatusPresenter<Status> {

    public StoryPresenter(PagedStatusView<Status> view) {
        super(view);
    }

    @Override
    public void load(User user) {
        statusService.loadMoreStory(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem, new GetStoryObserver(this));
    }

    private class GetStoryObserver extends PagedObserver<Status> {
        public GetStoryObserver(PagedPresenter<Status> presenter) {
            super(presenter);
        }

        @Override
        protected void showError(String message) {
            view.displayMessage("Failed to get story: " + message);
        }

        @Override
        protected void showException(Exception ex) {
            view.displayMessage("Failed to get story because of exception: " + ex.getMessage());
        }
    }

}
