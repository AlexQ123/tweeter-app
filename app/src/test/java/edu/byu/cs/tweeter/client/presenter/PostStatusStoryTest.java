package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.presenter.view.MainView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

public class PostStatusStoryTest {

    private MainView mockView;
    private MainPresenter mainPresenterSpy;
    private StatusService mockStatusService;
    private ServerFacade serverFacade;

    @BeforeEach
    public void setup() {
        mockView = Mockito.mock(MainView.class);
        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));
        mockStatusService = Mockito.mock(StatusService.class);
        serverFacade = new ServerFacade();

        Mockito.when(mainPresenterSpy.getStatusService()).thenReturn(mockStatusService);

        // Login a user.
        try {
            AuthenticateResponse loginResponse = serverFacade.login(new LoginRequest("@a", "a"), "/login");
            Cache.getInstance().setCurrUser(loginResponse.getUser());
            Cache.getInstance().setCurrUserAuthToken(loginResponse.getAuthToken());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testPostStatus_postStatusSuccessful() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                AuthToken authToken = invocation.getArgument(0, AuthToken.class);
                Assertions.assertNotNull(authToken);

                Status status = invocation.getArgument(1, Status.class);
                Assertions.assertEquals(status.getPost(), "Test");

                MainPresenter.PostStatusObserver observer = invocation.getArgument(2, MainPresenter.PostStatusObserver.class);
                Assertions.assertNotNull(observer);

                PostStatusResponse response = serverFacade.postStatus(new PostStatusRequest(authToken, status), "/poststatus");
                if (response.isSuccess()) {
                    observer.handleSuccess();
                }

                return null;
            }
        };

        // Post a status from the user to the server by calling the "post status" operation on the relevant Presenter.
        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());
        mainPresenterSpy.postStatus("Test");
        // Verify that the "Successfully Posted!" message was displayed to the user.
        Mockito.verify(mockView).displayMessage("Successfully Posted!");
    }

    @Test
    public void testPostStatus_properlyAddedToStory() {
        // Retrieve the user's story from the server to verify that the new status was correctly appended to the user's story, and that all status details are correct.
        try {
            GetStoryResponse response = serverFacade.getStory(new GetStoryRequest(Cache.getInstance().getCurrUserAuthToken(), "@a", 25, null), "/getstory");
            Assertions.assertNotNull(response.getStory().get(0));
            Assertions.assertEquals("Test", response.getStory().get(0).getPost());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

}
