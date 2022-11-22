package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public interface FeedDAO {

    Pair<List<Status>, Boolean> getPagedFeed(String targetUserAlias, int pageSize, Status lastStatus);
    void addStatus(Status status);
    void batchUpdateFeed(Status status, List<User> followers);

}
