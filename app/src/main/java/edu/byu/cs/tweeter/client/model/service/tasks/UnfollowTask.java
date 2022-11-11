package edu.byu.cs.tweeter.client.model.service.tasks;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthenticatedTask {

    public static final String URL_PATH = "/unfollow";

    /**
     * The user that is being followed.
     */
    private final User followee;

    public UnfollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
    }

    @Override
    protected void runTask() throws IOException, TweeterRemoteException {
        // We could do this from the presenter, without a task and handler, but we will
        // eventually access the database from here when we aren't using dummy data.

        UnfollowRequest request = new UnfollowRequest(getAuthToken(), Cache.getInstance().getCurrUser().getAlias(), followee.getAlias());
        UnfollowResponse response = getServerFacade().unfollow(request, URL_PATH);

        if (response.isSuccess()) {
            sendSuccessMessage();
        }
        else {
            sendFailedMessage(response.getMessage());
        }
    }


}
