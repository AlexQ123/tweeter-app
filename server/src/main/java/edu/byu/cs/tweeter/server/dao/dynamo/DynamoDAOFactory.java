package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.ImageDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class DynamoDAOFactory implements DAOFactory {

    @Override
    public UserDAO createUserDAO() {
        return new DynamoUserDAO();
    }

    @Override
    public ImageDAO createImageDAO() {
        return new S3ImageDAO();
    }

    @Override
    public AuthTokenDAO createAuthTokenDAO() {
        return new DynamoAuthTokenDAO();
    }

    @Override
    public FollowsDAO createFollowsDAO() { return new DynamoFollowsDAO(); }

    @Override
    public FeedDAO createFeedDAO() { return new DynamoFeedDAO(); }

    @Override
    public StoryDAO createStoryDAO() { return new DynamoStoryDAO(); }

}
