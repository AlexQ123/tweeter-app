package edu.byu.cs.tweeter.client.model.service.tasks.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.presenter.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.tasks.PagedTask;

public class PagedHandler<T> extends BackgroundTaskHandler<PagedObserver<T>> {
    public PagedHandler(PagedObserver<T> observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(PagedObserver<T> observer, Bundle data) {
        List<T> items = (List<T>) data.getSerializable(PagedTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(PagedTask.MORE_PAGES_KEY);
        observer.handleSuccess(items, hasMorePages);
    }
}
