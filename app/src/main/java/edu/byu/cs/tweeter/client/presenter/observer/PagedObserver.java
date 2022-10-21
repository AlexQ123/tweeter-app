package edu.byu.cs.tweeter.client.presenter.observer;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.client.presenter.PagedPresenter;

public abstract class PagedObserver<T> implements ServiceObserver {

    PagedPresenter<T> presenter;

    public PagedObserver(PagedPresenter<T> presenter) {
        this.presenter = presenter;
    }

    public void handleSuccess(List<T> items, boolean hasMorePages) {
        presenter.setLoading(false);
        presenter.getView().setLoadingFooter(false);
        presenter.setLastItem( (items.size() > 0) ? items.get(items.size() - 1) : null );
        presenter.getView().addItems(items);
        presenter.setHasMorePages(hasMorePages);
    }

    @Override
    public void displayErrorMessage(String message) {
        presenter.setLoading(false);
        showError(message);
        presenter.getView().setLoadingFooter(false);
    }

    protected abstract void showError(String message);

    @Override
    public void displayException(Exception ex) {
        presenter.setLoading(false);
        showException(ex);
        presenter.getView().setLoadingFooter(false);
    }

    protected abstract void showException(Exception ex);

}
