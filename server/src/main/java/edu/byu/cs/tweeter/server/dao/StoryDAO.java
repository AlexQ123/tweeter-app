package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.Pair;

public interface StoryDAO {

    Pair<List<Status>, Boolean> getPagedStory(String targetUserAlias, int pageSize, Status lastStatus);
    void addStatus(Status status);

}
