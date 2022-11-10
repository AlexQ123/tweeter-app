package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public interface FollowsDAO {

    Pair<List<User>, Boolean> getFollowees(String followerHandle, int pageSize, String lastFollowee);
    Pair<List<User>, Boolean> getFollowers(String followeeHandle, int pageSize, String lastFollower);

}
