package edu.byu.cs.tweeter.client.model.service.tasks.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.presenter.observer.AuthenticateObserver;
import edu.byu.cs.tweeter.client.model.service.tasks.AuthenticateTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class AuthenticateHandler extends BackgroundTaskHandler<AuthenticateObserver> {
    public AuthenticateHandler(AuthenticateObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(AuthenticateObserver observer, Bundle data) {
        User user = (User) data.getSerializable(AuthenticateTask.USER_KEY);
        AuthToken authToken = (AuthToken) data.getSerializable(AuthenticateTask.AUTH_TOKEN_KEY);
        observer.handleSuccess(user, authToken);
    }
}
