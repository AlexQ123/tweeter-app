package edu.byu.cs.tweeter.client.model.service.tasks;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends AuthenticatedTask {

    public static final String URL_PATH = "/logout";

    public LogoutTask(AuthToken authToken, Handler messageHandler) {
        super(authToken, messageHandler);
    }

    @Override
    protected void runTask() throws IOException, TweeterRemoteException {
        // We could do this from the presenter, without a task and handler, but we will
        // eventually remove the auth token from  the DB and will need this then.

        LogoutRequest request = new LogoutRequest(getAuthToken());
        LogoutResponse response = getServerFacade().logout(request, URL_PATH);

        if (response.isSuccess()) {
            sendSuccessMessage();
        }
        else {
            sendFailedMessage(response.getMessage());
        }
    }

}
