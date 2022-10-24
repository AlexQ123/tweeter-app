package edu.byu.cs.tweeter.client.model.net;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetCountRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.GetCountResponse;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class ServerFacadeIntegrationTest {

    private ServerFacade serverFacade;
    private FakeData fakeData;

    String registerURL = "/register";
    String getFollowersURL = "/getfollowers";
    String getFollowersCountURL = "/getfollowerscount";

    @BeforeEach
    public void setup() {
        serverFacade = new ServerFacade();
        fakeData = FakeData.getInstance();
    }

    @Test
    public void registerSuccess() throws IOException, TweeterRemoteException {
        RegisterRequest request = new RegisterRequest("test", "test", "test", "test", "test");
        AuthenticateResponse response = serverFacade.register(request, registerURL);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNotNull(response.getUser());
        Assertions.assertNotNull(response.getAuthToken());
    }

    @Test
    public void registerFail() {
        RegisterRequest request = new RegisterRequest(null, null, null, null, null);

        Exception ex = Assertions.assertThrows(TweeterRemoteException.class, () -> serverFacade.register(request, registerURL));
        Assertions.assertEquals("[Bad Request] Missing a first name", ex.getMessage());
        request.setFirstName("test");

        ex = Assertions.assertThrows(TweeterRemoteException.class, () -> serverFacade.register(request, registerURL));
        Assertions.assertEquals("[Bad Request] Missing a last name", ex.getMessage());
        request.setLastName("test");

        ex = Assertions.assertThrows(TweeterRemoteException.class, () -> serverFacade.register(request, registerURL));
        Assertions.assertEquals("[Bad Request] Missing an image", ex.getMessage());
        request.setImage("test");

        ex = Assertions.assertThrows(TweeterRemoteException.class, () -> serverFacade.register(request, registerURL));
        Assertions.assertEquals("[Bad Request] Missing a username", ex.getMessage());
        request.setUsername("test");

        ex = Assertions.assertThrows(TweeterRemoteException.class, () -> serverFacade.register(request, registerURL));
        Assertions.assertEquals("[Bad Request] Missing a password", ex.getMessage());
    }

    @Test
    public void getFollowersSuccess() throws IOException, TweeterRemoteException {
        int pageSize = 21;
        GetFollowersRequest request = new GetFollowersRequest(fakeData.getAuthToken(), "test", pageSize, "test");
        GetFollowersResponse response = serverFacade.getFollowers(request, getFollowersURL);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(pageSize, response.getFollowers().size());
        Assertions.assertEquals(fakeData.getFakeUsers(), response.getFollowers());
    }

    @Test
    public void getFollowersFail() {
        GetFollowersRequest request = new GetFollowersRequest(fakeData.getAuthToken(), null, 0, "test");

        Exception ex = Assertions.assertThrows(TweeterRemoteException.class, () -> serverFacade.getFollowers(request, getFollowersURL));
        Assertions.assertEquals("[Bad Request] Request needs to have a followee alias", ex.getMessage());
        request.setFolloweeAlias("test");

        ex = Assertions.assertThrows(TweeterRemoteException.class, () -> serverFacade.getFollowers(request, getFollowersURL));
        Assertions.assertEquals("[Bad Request] Request needs to have a positive limit", ex.getMessage());
    }

    @Test
    public void getFollowersCountSuccess() throws IOException, TweeterRemoteException {
        GetCountRequest request = new GetCountRequest(fakeData.getAuthToken(), "test");
        GetCountResponse response = serverFacade.getFollowersCount(request, getFollowersCountURL);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(fakeData.getFakeUsers().size(), response.getCount());
    }

    @Test
    public void getFollowersCountFail() {
        GetCountRequest request = new GetCountRequest(fakeData.getAuthToken(), null);
        Exception ex = Assertions.assertThrows(TweeterRemoteException.class, () -> serverFacade.getFollowersCount(request, getFollowersCountURL));
        Assertions.assertEquals("[Bad Request] Request needs to have a target user alias", ex.getMessage());
    }

}
