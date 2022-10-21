package edu.byu.cs.tweeter.client.presenter.view;

public interface MainView extends BaseView {
    void displayFollowButton(boolean isFollower);

    void updateAfterUnfollow();
    void updateAfterFollow();
    void enableFollowButton(boolean value);

    void setFollowerCountText(int count);
    void setFollowingCountText(int count);

    void logout();

    void cancelPostingToast();
}
