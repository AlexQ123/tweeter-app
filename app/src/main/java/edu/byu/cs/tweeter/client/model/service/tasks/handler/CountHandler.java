package edu.byu.cs.tweeter.client.model.service.tasks.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.observer.CountObserver;
import edu.byu.cs.tweeter.client.model.service.tasks.GetCountTask;

public class CountHandler extends BackgroundTaskHandler<CountObserver> {
    public CountHandler(CountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(CountObserver observer, Bundle data) {
        int count = data.getInt(GetCountTask.COUNT_KEY);
        observer.handleSuccess(count);
    }
}
