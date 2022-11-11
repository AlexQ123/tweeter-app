package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public interface FollowsDAO {

    Pair<List<User>, Boolean> getPagedFollowees(String followerHandle, int pageSize, String lastFollowee);
    Pair<List<User>, Boolean> getPagedFollowers(String followeeHandle, int pageSize, String lastFollower);
    boolean checkIsFollower(String followerAlias, String followeeAlias);
    void deleteFollows(String followerAlias, String followeeAlias);
    void addFollows(User follower, User followee);

}
