package edu.byu.cs.tweeter.client.presenter.observer;

import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.presenter.view.MainView;

public abstract class FollowActionObserver implements SimpleNotificationObserver {

    private MainView view;

    public FollowActionObserver(MainView view) {
        this.view = view;
    }

    public void handleSuccess() {
        update();
        view.enableFollowButton(true);
    }

    protected abstract void update();

    @Override
    public void displayErrorMessage(String message) {
        showError(message);
        view.enableFollowButton(true);
    }

    protected abstract void showError(String message);

    @Override
    public void displayException(Exception ex) {
        showException(ex);
        view.enableFollowButton(true);
    }

    protected abstract void showException(Exception ex);

}
