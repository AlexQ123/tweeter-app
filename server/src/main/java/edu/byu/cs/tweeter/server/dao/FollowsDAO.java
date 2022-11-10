package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public interface FollowsDAO {

    List<User> getFollowees(String followerHandle, int pageSize, String lastFollowee);
    List<User> getFollowers(String followeeHandle, int pageSize, String lastFollower);

}
