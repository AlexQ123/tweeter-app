package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.tasks.handler.PagedHandler;
import edu.byu.cs.tweeter.client.model.service.tasks.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.presenter.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.tasks.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.tasks.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.tasks.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.tasks.PostStatusTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {

    public void loadMoreFeed(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, PagedObserver<Status> getFeedObserver) {
        GetFeedTask getFeedTask = new GetFeedTask(currUserAuthToken, user, pageSize, lastStatus, new PagedHandler<>(getFeedObserver));
        BackgroundTaskUtils.runTask(getFeedTask);
    }

    public void loadMoreStory(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, PagedObserver<Status> getStoryObserver) {
        GetStoryTask getStoryTask = new GetStoryTask(currUserAuthToken, user, pageSize, lastStatus, new PagedHandler<>(getStoryObserver));
        BackgroundTaskUtils.runTask(getStoryTask);
    }

    public void postStatus(AuthToken currUserAuthToken, Status newStatus, SimpleNotificationObserver postStatusObserver) {
        PostStatusTask statusTask = new PostStatusTask(currUserAuthToken, newStatus, new SimpleNotificationHandler(postStatusObserver));
        BackgroundTaskUtils.runTask(statusTask);
    }

}
