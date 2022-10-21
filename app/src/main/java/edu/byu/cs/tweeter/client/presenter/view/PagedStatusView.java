package edu.byu.cs.tweeter.client.presenter.view;

public interface PagedStatusView<T> extends PagedView<T>{
    void startExternal(String clickable);
}
