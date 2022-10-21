package edu.byu.cs.tweeter.client.model.service.tasks.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;

public class SimpleNotificationHandler extends BackgroundTaskHandler<SimpleNotificationObserver>{
    public SimpleNotificationHandler(SimpleNotificationObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(SimpleNotificationObserver observer, Bundle data) {
        observer.handleSuccess();
    }
}
