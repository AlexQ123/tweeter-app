package edu.byu.cs.tweeter.client.presenter.view;

import edu.byu.cs.tweeter.model.domain.User;

public interface AuthenticateView extends BaseView {
    void startMain(User loggedInUser, String name);
}
