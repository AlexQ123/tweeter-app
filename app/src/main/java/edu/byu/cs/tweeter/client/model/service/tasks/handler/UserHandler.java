package edu.byu.cs.tweeter.client.model.service.tasks.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.observer.UserObserver;
import edu.byu.cs.tweeter.client.model.service.tasks.GetUserTask;
import edu.byu.cs.tweeter.model.domain.User;

public class UserHandler extends BackgroundTaskHandler<UserObserver> {
    public UserHandler(UserObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(UserObserver observer, Bundle data) {
        User user = (User) data.getSerializable(GetUserTask.USER_KEY);
        observer.handleSuccess(user);
    }
}