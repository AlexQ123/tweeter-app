package edu.byu.cs.tweeter.client.model.service.observer;

public interface ServiceObserver {
    void displayErrorMessage(String message);
    void displayException(Exception exception);
}