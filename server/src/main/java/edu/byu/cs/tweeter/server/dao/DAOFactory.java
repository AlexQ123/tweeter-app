package edu.byu.cs.tweeter.server.dao;

public interface DAOFactory {

    UserDAO createUserDAO();
    ImageDAO createImageDAO();
    AuthTokenDAO createAuthTokenDAO();
    FollowsDAO createFollowsDAO();
    FeedDAO createFeedDAO();
    StoryDAO createStoryDAO();

}
