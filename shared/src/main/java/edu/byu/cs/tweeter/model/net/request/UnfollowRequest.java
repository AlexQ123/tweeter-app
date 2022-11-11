package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UnfollowRequest {

    private AuthToken authToken;
    private String currentUserAlias;
    private String followeeAlias;

    private UnfollowRequest() {}

    public UnfollowRequest(AuthToken authToken, String currentUserAlias, String followeeAlias) {
        this.authToken = authToken;
        this.currentUserAlias = currentUserAlias;
        this.followeeAlias = followeeAlias;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getFolloweeAlias() {
        return followeeAlias;
    }

    public void setFolloweeAlias(String followeeAlias) {
        this.followeeAlias = followeeAlias;
    }

    public String getCurrentUserAlias() {
        return currentUserAlias;
    }

    public void setCurrentUserAlias(String currentUserAlias) {
        this.currentUserAlias = currentUserAlias;
    }
}
