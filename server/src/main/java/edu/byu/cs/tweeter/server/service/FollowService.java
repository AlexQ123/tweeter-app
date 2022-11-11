package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.GetCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.GetCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.util.Pair;

public class FollowService extends Service {

    public FollowService(DAOFactory daoFactory) {
        super(daoFactory);
    }

    public GetFollowingResponse getFollowees(GetFollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        }
        if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        // Check for bad/expired authtoken
        if (expiredToken(request.getAuthToken().getToken())) {
            return new GetFollowingResponse("Session expired, please log out and log in again.");
        }

        Pair<List<User>, Boolean> daoResponse = followsDAO.getPagedFollowees(request.getFollowerAlias(), request.getLimit(), request.getLastFolloweeAlias());
        return new GetFollowingResponse(daoResponse.getFirst(), daoResponse.getSecond());
    }

    public GetFollowersResponse getFollowers(GetFollowersRequest request) {
        if (request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        // Check for bad/expired authtoken
        if (expiredToken(request.getAuthToken().getToken())) {
            return new GetFollowersResponse("Session expired, please log out and log in again.");
        }

        Pair<List<User>, Boolean> daoResponse = followsDAO.getPagedFollowers(request.getFolloweeAlias(), request.getLimit(), request.getLastFollowerAlias());
        return new GetFollowersResponse(daoResponse.getFirst(), daoResponse.getSecond());
    }

    public IsFollowerResponse determineIsFollower(IsFollowerRequest request) {
        if (request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        }
        if (request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }

        // Check for bad/expired authtoken
        if (expiredToken(request.getAuthToken().getToken())) {
            return new IsFollowerResponse("Session expired, please log out and log in again.");
        }

        boolean isFollower = followsDAO.checkIsFollower(request.getFollowerAlias(), request.getFolloweeAlias());
        return new IsFollowerResponse(isFollower);
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if (request.getCurrentUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have the current user's alias.");
        }
        if (request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias.");
        }

        // Check for bad/expired authtoken
        if (expiredToken(request.getAuthToken().getToken())) {
            return new UnfollowResponse("Session expired, please log out and log in again.");
        }

        followsDAO.deleteFollows(request.getCurrentUserAlias(), request.getFolloweeAlias());
        return new UnfollowResponse();
    }

    public FollowResponse follow(FollowRequest request) {
        if (request.getCurrentUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have the current user.");
        }
        if (request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee.");
        }

        // Check for bad/expired authtoken
        if (expiredToken(request.getAuthToken().getToken())) {
            return new FollowResponse("Session expired, please log out and log in again.");
        }

        followsDAO.addFollows(request.getCurrentUser(), request.getFollowee());
        return new FollowResponse();
    }

    public GetCountResponse getFollowersCount(GetCountRequest request) {
        if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user alias");
        }

        // Check for bad/expired authtoken
        if (expiredToken(request.getAuthToken().getToken())) {
            return new GetCountResponse("Session expired, please log out and log in again.");
        }

        return new GetCountResponse(followsDAO.getAllFollowers(request.getTargetUserAlias()).size());
    }

    public GetCountResponse getFollowingCount(GetCountRequest request) {
        if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user alias");
        }

        // Check for bad/expired authtoken
        if (expiredToken(request.getAuthToken().getToken())) {
            return new GetCountResponse("Session expired, please log out and log in again.");
        }

        return new GetCountResponse(followsDAO.getAllFollowees(request.getTargetUserAlias()).size());
    }

}
