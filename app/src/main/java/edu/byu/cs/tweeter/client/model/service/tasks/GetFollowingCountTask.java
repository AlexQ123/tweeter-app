package edu.byu.cs.tweeter.client.model.service.tasks;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetCountRequest;
import edu.byu.cs.tweeter.model.net.response.GetCountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {

    public static final String URL_PATH = "/getfollowingcount";

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected GetCountResponse runCountTask() throws IOException, TweeterRemoteException {
        GetCountRequest request = new GetCountRequest(getAuthToken(), getTargetUser().getAlias());

        return getServerFacade().getFollowingCount(request, URL_PATH);
    }

//    @Override
//    protected int runCountTask() {
//        return 20;
//    }
}
