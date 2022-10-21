package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.presenter.observer.AuthenticateObserver;
import edu.byu.cs.tweeter.client.presenter.view.AuthenticateView;

public class LoginPresenter extends AuthenticatePresenter {

    public LoginPresenter(AuthenticateView view) {
        super(view);
    }

    public void login(String alias, String password) {
        userService.login(alias, password, new LoginObserver(view));
    }

    private class LoginObserver extends AuthenticateObserver {
        public LoginObserver(AuthenticateView view) {
            super(view);
        }

        @Override
        public void displayErrorMessage(String message) {
            view.displayMessage("Failed to login: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to login because of exception: " + ex.getMessage());
        }
    }

}
