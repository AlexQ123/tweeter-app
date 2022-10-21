package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;

public abstract class Presenter {

    protected UserService userService;
    protected FollowService followService;
    protected StatusService statusService;

    public Presenter() {
        userService = new UserService();
        followService = new FollowService();
        statusService = getStatusService();
    }

    protected StatusService getStatusService() {
        if (statusService == null) {
            statusService = new StatusService();
        }
        return statusService;
    }

}
