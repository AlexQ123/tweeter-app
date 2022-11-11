package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.observer.CheckFollowerObserver;
import edu.byu.cs.tweeter.client.model.service.observer.CountObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.presenter.observer.FollowActionObserver;
import edu.byu.cs.tweeter.client.presenter.view.MainView;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter {

    private static final String LOG_TAG = "MainActivity";

    private MainView view;

    public MainPresenter(MainView view) {
        this.view = view;
    }

    public boolean isCurrentUser(User selectedUser) {
        return selectedUser.compareTo(Cache.getInstance().getCurrUser()) == 0;
    }

    public void determineIsFollower(User selectedUser) {
        followService.determineIsFollower(Cache.getInstance().getCurrUserAuthToken(), Cache.getInstance().getCurrUser(),
                selectedUser, new IsFollowerObserver());
    }

    public void unfollow(User selectedUser) {
        followService.unfollow(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new UnfollowObserver(view));
    }

    public void follow(User selectedUser) {
        followService.follow(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new FollowObserver(view));
    }

    public void getFollowersCount(User selectedUser) {
        followService.getFollowersCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new GetFollowersCountObserver());
    }

    public void getFollowingCount(User selectedUser) {
        followService.getFollowingCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new GetFollowingCountObserver());
    }

    public void logout() {
        userService.logout(Cache.getInstance().getCurrUserAuthToken(), new LogoutObserver());
    }

    public void postStatus(String post) {
        try {
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
            getStatusService().postStatus(Cache.getInstance().getCurrUserAuthToken(), newStatus, new PostStatusObserver());
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            view.displayMessage("Failed to post the status because of exception: " + ex.getMessage());
        }
    }

    public String getFormattedDateTime() throws ParseException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.toString();

//        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");
//
//        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    private class IsFollowerObserver implements CheckFollowerObserver {
        @Override
        public void handleSuccess(boolean isFollower) {
            view.displayFollowButton(isFollower);
        }

        @Override
        public void displayErrorMessage(String message) {
            view.displayMessage("Failed to determine following relationship: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to determine following relationship because of exception: " + ex.getMessage());
        }
    }

    private class UnfollowObserver extends FollowActionObserver {
        public UnfollowObserver(MainView view) {
            super(view);
        }

        @Override
        protected void update() {
            view.updateAfterUnfollow();
        }

        @Override
        protected void showError(String message) {
            view.displayMessage("Failed to unfollow: " + message);
        }

        @Override
        protected void showException(Exception ex) {
            view.displayMessage("Failed to unfollow because of exception: " + ex.getMessage());
        }
    }

    private class FollowObserver extends FollowActionObserver {
        public FollowObserver(MainView view) {
            super(view);
        }

        @Override
        protected void update() {
            view.updateAfterFollow();
        }

        @Override
        protected void showError(String message) {
            view.displayMessage("Failed to follow: " + message);
        }

        @Override
        protected void showException(Exception ex) {
            view.displayMessage("Failed to follow because of exception: " + ex.getMessage());
        }
    }

    private class GetFollowersCountObserver implements CountObserver {
        @Override
        public void handleSuccess(int count) {
            view.setFollowerCountText(count);
        }

        @Override
        public void displayErrorMessage(String message) {
            view.displayMessage("Failed to get followers count: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to get followers count because of exception: " + ex.getMessage());
        }
    }

    private class GetFollowingCountObserver implements CountObserver {
        @Override
        public void handleSuccess(int count) {
            view.setFollowingCountText(count);
        }

        @Override
        public void displayErrorMessage(String message) {
            view.displayMessage("Failed to get following count: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to get following count because of exception: " + ex.getMessage());
        }
    }

    private class LogoutObserver implements SimpleNotificationObserver {
        @Override
        public void handleSuccess() {
            view.logout();
        }

        @Override
        public void displayErrorMessage(String message) {
            view.displayMessage("Failed to logout: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to logout because of exception: " + ex.getMessage());
        }
    }

    protected class PostStatusObserver implements SimpleNotificationObserver {
        @Override
        public void handleSuccess() {
            view.cancelPostingToast();
            view.displayMessage("Successfully Posted!");
        }

        @Override
        public void displayErrorMessage(String message) {
            view.displayMessage("Failed to post status: " + message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to post status because of exception: " + ex.getMessage());
        }
    }

}
