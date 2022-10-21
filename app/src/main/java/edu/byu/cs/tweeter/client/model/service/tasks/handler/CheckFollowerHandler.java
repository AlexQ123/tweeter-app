package edu.byu.cs.tweeter.client.model.service.tasks.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.observer.CheckFollowerObserver;
import edu.byu.cs.tweeter.client.model.service.tasks.IsFollowerTask;

public class CheckFollowerHandler extends BackgroundTaskHandler<CheckFollowerObserver> {
    public CheckFollowerHandler(CheckFollowerObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(CheckFollowerObserver observer, Bundle data) {
        boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
        observer.handleSuccess(isFollower);
    }
}