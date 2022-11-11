package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService extends Service {

    public StatusService(DAOFactory daoFactory) {
        super(daoFactory);
    }

    public GetFeedResponse getFeed(GetFeedRequest request) {
        if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        Pair<List<Status>, Boolean> statuses = FakeData.getInstance().getPageOfStatus(request.getLastStatus(), request.getLimit());
        return new GetFeedResponse(statuses.getFirst(), statuses.getSecond());
    }

    public GetStoryResponse getStory(GetStoryRequest request) {
        if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        Pair<List<Status>, Boolean> statuses = FakeData.getInstance().getPageOfStatus(request.getLastStatus(), request.getLimit());
        return new GetStoryResponse(statuses.getFirst(), statuses.getSecond());
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        if (request.getStatus() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a status");
        }

        // Check for bad/expired authtoken
        if (expiredToken(request.getAuthToken().getToken())) {
            return new PostStatusResponse("Session expired, please log out and log in again.");
        }

        // what you need to store in the DB when posting a status:
        // Feed: receiver_alias, timestamp, post, first_name, last_name, image, urls, mentions
        // Story: sender_alias, timestamp, post, first_name, last_name, image, urls, mentions
        storyDAO.addStatus(request.getStatus());
        feedDAO.addStatus(request.getStatus());

        return new PostStatusResponse();
    }

}
