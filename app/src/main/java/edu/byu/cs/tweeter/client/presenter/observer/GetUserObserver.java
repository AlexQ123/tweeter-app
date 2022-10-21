package edu.byu.cs.tweeter.client.presenter.observer;

import edu.byu.cs.tweeter.client.model.service.observer.UserObserver;
import edu.byu.cs.tweeter.client.presenter.view.PagedView;
import edu.byu.cs.tweeter.model.domain.User;

public class GetUserObserver<T> implements UserObserver {

    private PagedView<T> view;

    public GetUserObserver(PagedView<T> view) {
        this.view = view;
    }

    @Override
    public void handleSuccess(User user) {
        view.startUser(user);
    }

    @Override
    public void displayErrorMessage(String message) {
        view.displayMessage("Failed to get user's profile: " + message);
    }

    @Override
    public void displayException(Exception ex) {
        view.displayMessage("Failed to get user's profile because of exception: " + ex.getMessage());
    }

    @Override
    public void displayGettingUser() {
        view.displayMessage("Getting user's profile...");
    }
}

