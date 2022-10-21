package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.tasks.handler.CheckFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.tasks.handler.CountHandler;
import edu.byu.cs.tweeter.client.model.service.tasks.handler.PagedHandler;
import edu.byu.cs.tweeter.client.model.service.tasks.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.observer.CheckFollowerObserver;
import edu.byu.cs.tweeter.client.model.service.observer.CountObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.presenter.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.tasks.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.tasks.FollowTask;
import edu.byu.cs.tweeter.client.model.service.tasks.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.tasks.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.tasks.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.tasks.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.tasks.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.tasks.UnfollowTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public void loadMoreFollowees(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, PagedObserver<User> getFollowingObserver) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(currUserAuthToken,
                user, pageSize, lastFollowee, new PagedHandler<>(getFollowingObserver));
        BackgroundTaskUtils.runTask(getFollowingTask);
    }

    public void loadMoreFollowers(AuthToken currUserAuthToken, User user, int pageSize, User lastFollower, PagedObserver<User> getFollowersObserver) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(currUserAuthToken,
                user, pageSize, lastFollower, new PagedHandler<>(getFollowersObserver));
        BackgroundTaskUtils.runTask(getFollowersTask);
    }

    public void determineIsFollower(AuthToken currUserAuthToken, User currUser, User selectedUser, CheckFollowerObserver isFollowerObserver) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(currUserAuthToken, currUser, selectedUser,
                new CheckFollowerHandler(isFollowerObserver));
        BackgroundTaskUtils.runTask(isFollowerTask);
    }

    public void unfollow(AuthToken currUserAuthToken, User selectedUser, SimpleNotificationObserver unfollowObserver) {
        UnfollowTask unfollowTask = new UnfollowTask(currUserAuthToken, selectedUser, new SimpleNotificationHandler(unfollowObserver));
        BackgroundTaskUtils.runTask(unfollowTask);
    }

    public void follow(AuthToken currUserAuthToken, User selectedUser, SimpleNotificationObserver followObserver) {
        FollowTask followTask = new FollowTask(currUserAuthToken, selectedUser, new SimpleNotificationHandler(followObserver));
        BackgroundTaskUtils.runTask(followTask);
    }

    public void getFollowersCount(AuthToken currUserAuthToken, User selectedUser, CountObserver getFollowersCountObserver) {
        // Get count of most recently selected user's followers.
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(currUserAuthToken,
                selectedUser, new CountHandler(getFollowersCountObserver));
        BackgroundTaskUtils.runTask(followersCountTask);
    }

    public void getFollowingCount(AuthToken currUserAuthToken, User selectedUser, CountObserver getFollowingCountObserver) {
        // Get count of most recently selected user's followees (who they are following)
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(currUserAuthToken,
                selectedUser, new CountHandler(getFollowingCountObserver));
        BackgroundTaskUtils.runTask(followingCountTask);
    }

}
