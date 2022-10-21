package edu.byu.cs.tweeter.client.presenter.observer;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.client.presenter.view.AuthenticateView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthenticateObserver implements ServiceObserver {

    private AuthenticateView view;

    public AuthenticateObserver(AuthenticateView view) {
        this.view = view;
    }

    public void handleSuccess(User user, AuthToken authToken) {
        Cache.getInstance().setCurrUser(user);
        Cache.getInstance().setCurrUserAuthToken(authToken);

        view.startMain(user, Cache.getInstance().getCurrUser().getName());
    }
}
